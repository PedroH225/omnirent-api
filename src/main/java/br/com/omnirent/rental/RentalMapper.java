 package br.com.omnirent.rental;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.omnirent.address.AddressMapper;
import br.com.omnirent.address.domain.AddressSnapshot;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.ItemMapper;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.dto.RentalResponseDTO;
import br.com.omnirent.user.domain.User;

public class RentalMapper {

	public static RentalResponseDTO toDto(Rental rental) {
		return new RentalResponseDTO(rental);
	}
	
	public static List<RentalResponseDTO> toDto(List<Rental> rentals) {
		return rentals.stream()
				.map(RentalResponseDTO::new)
				.collect(Collectors.toList());
	}
	
	public static Rental create(User renter, User owner, Item item,
		    RentalPeriod rentalPeriod, RentalStatus rentalStatus,
		    BigDecimal finalPrice) {
		Rental rental = new Rental();
		
		rental.assignOwner(owner);
		rental.assignRenter(renter);
		
		rental.setRentalPeriod(rentalPeriod);
		
		rental.setRentalStatus(rentalStatus);
		
		rental.setFinalPrice(finalPrice);
		
		rental.setAddressSnapshot(AddressMapper.fromAddress(item.getPickupAddress(), rental));
		rental.setItemSnapshot(ItemMapper.fromItem(item, rental));
		
		return rental;
	}
	
	public static Rental setDates(Rental rental, LocalDateTime startDate, LocalDateTime endDate) {
		rental.setStartDate(startDate);
		rental.setEndDate(endDate);
		
		return rental;
	}
}
