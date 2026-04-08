package br.com.omnirent.rental;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.domain.RentalNotFoundException;
import br.com.omnirent.item.Item;
import br.com.omnirent.item.ItemService;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.domain.RentalAuthorizationService;
import br.com.omnirent.rental.domain.RentalDateService;
import br.com.omnirent.rental.domain.RentalPriceService;
import br.com.omnirent.rental.domain.RentalRequestDTO;
import br.com.omnirent.rental.domain.RentalResponseDTO;
import br.com.omnirent.user.User;
import br.com.omnirent.user.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalService {

	private RentalRepository rentalRepository;
	
	private ItemService itemService;
	
	private UserService userService;
	
	private RentalAuthorizationService authorizationService;
	
	public Rental findById(String id) {
		Optional<Rental> rental = rentalRepository.findById(id);
		
		if (rental.isEmpty()) {
			throw new RentalNotFoundException();
		}
		
		return rental.get();
	}
	
	public RentalResponseDTO getRentalById(String id) {
		return RentalMapper.toDto(findById(id));
	}

	public RentalResponseDTO addRent(RentalRequestDTO rentalRequestDTO, String userId) {
		User renter = userService.findById(userId);
		Item item = itemService.findById(rentalRequestDTO.itemId());
		User owner = item.getOwner();
		
		RentalStatus rentalStatus = RentalStatus.CREATED;
		RentalPeriod rentalPeriod = RentalPeriod.fromString(rentalRequestDTO.rentalPeriod());
		
		BigDecimal finalPrice = RentalPriceService.calculateFinalPrice(item, rentalPeriod);
		
		Rental rental = RentalMapper.create(renter, owner, item,
			    rentalPeriod, rentalStatus,
			    finalPrice);
		
		return RentalMapper.toDto(rentalRepository.save(rental));
	}

	@Transactional
	public RentalResponseDTO updateStatus(String rentId, String status) {
		Rental rent = findById(rentId);
		
		rent.updateStatus(status);
		
		return RentalMapper.toDto(rentalRepository.save(rent));
	}

	@Transactional
	public RentalResponseDTO startPreparing(String rentId, String currentUserId) {
		Rental rental = findById(rentId);
		
		authorizationService.requireOwner(rental, currentUserId);

		rental.startPreparing();
		return RentalMapper.toDto(rentalRepository.save(rental));
	}

	@Transactional
	public RentalResponseDTO ship(String rentId, String currentUserId) {
		Rental rental = findById(rentId);
		
		authorizationService.requireOwner(rental, currentUserId);

		rental.ship();				
		return RentalMapper.toDto(rentalRepository.save(rental));
	}

	@Transactional
	public RentalResponseDTO markInUse(String rentId, String userId) {
		Rental rental = findById(rentId);
		rental.markInUse();
		
		// TEMPORARY
		authorizationService.requireOne(rental, userId);
		
		LocalDateTime startDate = LocalDateTime.now();
		LocalDateTime endDateTime = RentalDateService.
				calculateEndDate(startDate, rental.getRentalPeriod());
		
		Rental updatedRental = RentalMapper.setDates(rental, startDate, endDateTime);
		
		return RentalMapper.toDto(rentalRepository.save(updatedRental));
	}

	@Transactional
	public RentalResponseDTO requestReturn(String rentId, String userId) {
		Rental rental = findById(rentId);
		
		authorizationService.requireRenter(rental, userId);

		rental.requestReturn();				
		return RentalMapper.toDto(rentalRepository.save(rental));
	}
	
	@Transactional
	public RentalResponseDTO markReturnShipped(String rentId, String userId) {
		Rental rental = findById(rentId);
		
		authorizationService.requireRenter(rental, userId);

		rental.markReturnShipped();		
		return RentalMapper.toDto(rentalRepository.save(rental));
	}
	
	@Transactional
	public RentalResponseDTO markReturned(String rentId, String currentUserId) {
		Rental rental = findById(rentId);
		
		authorizationService.requireOwner(rental, currentUserId);
		
		rental.markReturned();
		return RentalMapper.toDto(rentalRepository.save(rental));
	}

	@Transactional
	public RentalResponseDTO cancel(String rentId, String userId) {
		Rental rental = findById(rentId);
		
		authorizationService.requireOne(rental, userId);
		
		rental.cancel();
		return RentalMapper.toDto(rentalRepository.save(rental));
	}

	@Transactional
	public RentalResponseDTO confirm(String rentId, String userId) {
		Rental rental = findById(rentId);
		
		// TEMPORARY
		authorizationService.requireOne(rental, userId);
		
		rental.confirm();
		return RentalMapper.toDto(rentalRepository.save(rental));
	}

	public List<RentalResponseDTO> findUserRented(String userId) {
		User user = userService.findById(userId);
		return RentalMapper.toDto(user.getRented());
	}

	public List<RentalResponseDTO> findUserRentals(String userId) {
		User user = userService.findById(userId);
		return RentalMapper.toDto(user.getRentals());
	}
}
