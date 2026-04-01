package br.com.omnirent.rental;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.Item;
import br.com.omnirent.item.ItemService;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.domain.RentalDateService;
import br.com.omnirent.rental.domain.RentalPriceService;
import br.com.omnirent.rental.domain.RentalRequestDTO;
import br.com.omnirent.rental.domain.RentalResponseDTO;
import br.com.omnirent.user.User;
import br.com.omnirent.user.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RentalService {

	private RentalRepository rentalRepository;
	
	private ItemService itemService;
	
	private UserService userService;
	
	public Rental findById(String id) {
		Optional<Rental> rental = rentalRepository.findById(id);
		
		if (rental.isEmpty()) {
			throw new RuntimeException("Rental not found.");
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
		
		RentalStatus rentalStatus = RentalStatus.ACTIVE;
		RentalPeriod rentalPeriod = RentalPeriod.fromString(rentalRequestDTO.rentalPeriod());
		
		LocalDateTime startDate = LocalDateTime.now();
		LocalDateTime endDateTime = RentalDateService.calculateEndDate(startDate, rentalPeriod);
		BigDecimal finalPrice = RentalPriceService.calculateFinalPrice(item, rentalPeriod);
		
		Rental rental = RentalMapper.create(renter, owner, item,
			    rentalPeriod, rentalStatus,
			    startDate, endDateTime, finalPrice);
		
		return RentalMapper.toDto(rentalRepository.save(rental));
	}

	public RentalResponseDTO updateStatus(String rentId, String status) {
		Rental rent = findById(rentId);
		
		rent.updateStatus(status);
		
		return RentalMapper.toDto(rentalRepository.save(rent));
	}
	
}
