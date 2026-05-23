package br.com.omnirent.rental;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.RentalEnums;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.RentalErrorType;
import br.com.omnirent.item.ItemService;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.rental.context.RentalStatusChangeContext;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.domain.RentalAuthorizationService;
import br.com.omnirent.rental.domain.RentalDateService;
import br.com.omnirent.rental.domain.RentalPriceService;
import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.rental.dto.RentalRequestDTO;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalService {
	
	private RentalRepository rentalRepository;
	
	private RentalQueryRepository queryRepository;
	
	private ItemService itemService;
	
	private UserService userService;
	
	private RentalAuthorizationService authorizationService;
	
	private RentalMapper mapper;
	
	private CurrentUserProvider currentUserProvider;
	
	private MessageService messageService;
	
	private void validateTransition(RentalStatus currStatus, RentalStatus targetStatus) {
		if (!currStatus.canTransition(targetStatus)) {
			throw new ApiException(RentalErrorType.ILLEGAL_STATE_TRANSITION,
					messageService.get(currStatus.getMessageKey()),
					messageService.get(targetStatus.getMessageKey()));
		}
	}

	public RentalDisplayDTO findRentalDisplayDTO(String id) {
		RentalDisplayDTO result = queryRepository.findRentalDisplayDTO(id)
				.orElseThrow(() -> new ApiException(RentalErrorType.NOT_FOUND));
		
		return mapper.localize(result);
	}
		
	public RentalDetailDTO getRentalById(String id) {
		RentalDetailDTO result = queryRepository.findRentalDetail(id)
				.orElseThrow(() -> new ApiException(RentalErrorType.NOT_FOUND));
		
		return mapper.localize(result);
	}
	
	private RentalStatusChangeContext getStatusChangeContext(String rentId) {
		return queryRepository.getStatusChangeContext(rentId)
				.orElseThrow(() -> new ApiException(RentalErrorType.NOT_FOUND));
	}

	public RentalCreatedDTO addRent(RentalRequestDTO rentalRequestDTO) {
		String userId = currentUserProvider.currentUserId();
		User renter = userService.getValidReference(userId);
		
		ItemRentedContext context = itemService.getItemRentedContext(rentalRequestDTO.itemId());
				
		ItemInfo itemInfo = context.getItemInfo();
		
		RentalStatus rentalStatus = RentalStatus.CREATED;
		RentalPeriod rentalPeriod = rentalRequestDTO.rentalPeriod();
		
		BigDecimal finalPrice = RentalPriceService.calculateFinalPrice(itemInfo.getBasePrice(), rentalPeriod);
		
		Rental rental = mapper.create(renter, userId, context,
			    rentalPeriod, rentalStatus,
			    finalPrice);
		
		return mapper.toCreatedDto(rentalRepository.save(rental));
	}

	@Transactional
	public void startPreparing(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);
		validateTransition(currStatus, RentalStatus.PREPARING);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.PREPARING);
	}

	@Transactional
	public void ship(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);
		validateTransition(currStatus, RentalStatus.SHIPPED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.SHIPPED);
	}

	@Transactional
	public RentalDisplayDTO markInUse(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();

		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		Set<String> actors = Set.of(context.getOwnerId(), context.getRenterId());
		
		// TEMPORARY
		authorizationService.requireOne(actors, currentUserId);
		validateTransition(currStatus, RentalStatus.IN_USE);
		
		LocalDateTime startDate = LocalDateTime.now();
		LocalDateTime endDateTime = RentalDateService.
				calculateEndDate(startDate, context.getRentalPeriod());
		
		rentalRepository.updateRentalPeriodAndStatus(rentId, RentalStatus.IN_USE, startDate, endDateTime);
		
		return findRentalDisplayDTO(rentId);
	}

	@Transactional
	public void requestReturn(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getRenterId()), currentUserId);
		validateTransition(currStatus, RentalStatus.RETURN_REQUESTED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.RETURN_REQUESTED);
	}
	
	@Transactional
	public void markReturnShipped(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getRenterId()), currentUserId);
		validateTransition(currStatus, RentalStatus.RETURN_SHIPPED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.RETURN_SHIPPED);
	}
	
	@Transactional
	public void markReturned(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		
		authorizationService.requireOne(Set.of(context.getOwnerId()), currentUserId);

		validateTransition(currStatus, RentalStatus.RETURNED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.RETURNED);
	}

	@Transactional
	public void cancel(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		Set<String> actors = Set.of(context.getOwnerId(), context.getRenterId());
		
		authorizationService.requireOne(actors, currentUserId);
		
		validateTransition(currStatus, RentalStatus.CANCELLED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.CANCELLED);
	}

	@Transactional
	public void confirm(String rentId) {
		String currentUserId = currentUserProvider.currentUserId();
		RentalStatusChangeContext context = getStatusChangeContext(rentId);
		
		RentalStatus currStatus = context.getRentalStatus();
		Set<String> actors = Set.of(context.getOwnerId(), context.getRenterId());
		
		// TEMPORARY
		authorizationService.requireOne(actors, currentUserId);
		validateTransition(currStatus, RentalStatus.CONFIRMED);
		
		rentalRepository.updateRentalStatus(rentId, RentalStatus.CONFIRMED);
	}

	public List<RentalDisplayDTO> findUserRented() {
		String renterId = currentUserProvider.currentUserId();
		userService.requireExistence(renterId);
		
		List<RentalDisplayDTO> result = queryRepository.findUserRented(renterId);
		
		return mapper.localize(result);
	}

	public List<RentalDisplayDTO> findUserRentals() {
		String ownerId = currentUserProvider.currentUserId();
		userService.requireExistence(ownerId);
		
		List<RentalDisplayDTO> result = queryRepository.findUserRentals(ownerId);
		
		return mapper.localize(result);
	}

	public RentalEnums getEnums() {
		return mapper.getLocalizedEnums();
	}
}
