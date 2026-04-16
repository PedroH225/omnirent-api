package br.com.omnirent.rental;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.NativeDetector.Context;
import org.springframework.stereotype.Service;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.domain.RentalNotFoundException;
import br.com.omnirent.item.ItemRepository;
import br.com.omnirent.item.ItemService;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.rental.context.RentalStatusChangeContext;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.domain.RentalAuthorizationService;
import br.com.omnirent.rental.domain.RentalDateService;
import br.com.omnirent.rental.domain.RentalPriceService;
import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.rental.dto.RentalRequestDTO;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import jakarta.transaction.Transactional;
import jdk.javadoc.internal.doclets.toolkit.taglets.SeeTaglet;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalService {
	
	private RentalRepository rentalRepository;
	
	private ItemService itemService;
	
	private UserService userService;
	
	private RentalAuthorizationService authorizationService;
	
	private RentalMapper mapper;
	
	public Rental findById(String id) {
		Optional<Rental> rental = rentalRepository.findById(id);
		
		if (rental.isEmpty()) {
			throw new RentalNotFoundException();
		}
		
		return rental.get();
	}


	public RentalDisplayDTO findRentalDisplayDTO(String id) {
		Optional<RentalDisplayDTO> optRental = rentalRepository.findRentalDisplayDTO(id);
		
		if (optRental.isEmpty()) {
			throw new RentalNotFoundException();
		}
		
		return optRental.get();
	}
		
	public RentalDetailDTO getRentalById(String id) {
		Optional<RentalDetailDTO> rOptional = rentalRepository.findRentalDetail(id);
		if (rOptional.isEmpty()) {
			throw new RentalNotFoundException();
		}
		
		return rOptional.get();
	}
	
	private RentalStatusChangeContext getStatusChangeContext(String rentId) {
		Optional<RentalStatusChangeContext> optContext = rentalRepository.getStatusChangeContext(rentId);
		
		if (optContext.isEmpty()) {
			throw new RentalNotFoundException();
		}
		return optContext.get();
	}

	public RentalCreatedDTO addRent(RentalRequestDTO rentalRequestDTO, String userId) {
		userService.requireExistence(userId);
		User renter = userService.getUserReference(userId);
		
		ItemRentedContext context = itemService.getItemRentedContext(rentalRequestDTO.itemId());
				
		ItemInfo itemInfo = context.getItemInfo();
		
		RentalStatus rentalStatus = RentalStatus.CREATED;
		RentalPeriod rentalPeriod = RentalPeriod.fromString(rentalRequestDTO.rentalPeriod());
		
		BigDecimal finalPrice = RentalPriceService.calculateFinalPrice(itemInfo.getBasePrice(), rentalPeriod);
		
		Rental rental = mapper.create(renter, userId, context,
			    rentalPeriod, rentalStatus,
			    finalPrice);
		
		return mapper.toCreatedDto(rentalRepository.save(rental));
	}

	@Transactional
	public RentalDisplayDTO updateStatus(String rentId, String status) {
		Rental rent = findById(rentId);
		
		rent.updateStatus(status);
		
		return new RentalDisplayDTO();
	}

	@Transactional
	public void startPreparing(String rentId, String currentUserId) {
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);
		currStatus.validateTransition(RentalStatus.PREPARING);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.PREPARING);
	}

	@Transactional
	public void ship(String rentId, String currentUserId) {
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);
		currStatus.validateTransition(RentalStatus.SHIPPED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.SHIPPED);
	}

	@Transactional
	public RentalDisplayDTO markInUse(String rentId, String userId) {
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		Set<String> actors = Set.of(context.getOwnerId(), context.getRenterId());
		
		// TEMPORARY
		authorizationService.requireOne(actors, userId);
		currStatus.validateTransition(RentalStatus.IN_USE);
		
		LocalDateTime startDate = LocalDateTime.now();
		LocalDateTime endDateTime = RentalDateService.
				calculateEndDate(startDate, context.getRentalPeriod());
		
		rentalRepository.updateRentalPeriodAndStatus(rentId, RentalStatus.IN_USE, startDate, endDateTime);
		
		return findRentalDisplayDTO(rentId);
	}

	@Transactional
	public void requestReturn(String rentId, String userId) {
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getRenterId()), userId);
		currStatus.validateTransition(RentalStatus.RETURN_REQUESTED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.RETURN_REQUESTED);
	}
	
	@Transactional
	public void markReturnShipped(String rentId, String userId) {
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getRenterId()), userId);
		currStatus.validateTransition(RentalStatus.RETURN_SHIPPED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.RETURN_SHIPPED);
	}
	
	@Transactional
	public void markReturned(String rentId, String currentUserId) {
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);

		currStatus.validateTransition(RentalStatus.RETURNED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.RETURNED);
	}

	@Transactional
	public RentalDisplayDTO cancel(String rentId, String userId) {
		Rental rental = findById(rentId);
		
		authorizationService.requireOne(rental, userId);
		
		rental.cancel();
		return new RentalDisplayDTO();
	}

	@Transactional
	public void confirm(String rentId, String userId) {
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		Set<String> actors = Set.of(context.getOwnerId(), context.getRenterId());
		
		// TEMPORARY
		authorizationService.requireOne(actors, userId);
		currStatus.validateTransition(RentalStatus.CONFIRMED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.CONFIRMED);
	}

	public List<RentalDisplayDTO> findUserRented(String userId) {
		User user = userService.findById(userId);
		return new ArrayList<RentalDisplayDTO>();
	}

	public List<RentalDisplayDTO> findUserRentals(String userId) {
		User user = userService.findById(userId);
		return new ArrayList<RentalDisplayDTO>();
	}
}
