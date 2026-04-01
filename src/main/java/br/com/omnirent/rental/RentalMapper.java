 package br.com.omnirent.rental;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.omnirent.address.AddressMapper;
import br.com.omnirent.address.AddressSnapshot;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.Item;
import br.com.omnirent.item.ItemMapper;
import br.com.omnirent.item.ItemSnapshot;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.domain.RentalResponseDTO;
import br.com.omnirent.user.User;

public class RentalMapper {

	public static RentalResponseDTO toDto(Rental rental) {
		return new RentalResponseDTO(rental);
	}
	
	public static Rental create(User renter, User owner, Item item,
		    RentalPeriod rentalPeriod, RentalStatus rentalStatus,
		    BigDecimal finalPrice) {
		Rental rental = new Rental();
		
		rental.setRenter(renter);
		rental.setOwner(owner);
		
		rental.setRentalPeriod(rentalPeriod);
		
		rental.setRentalStatus(rentalStatus);
		
		rental.setFinalPrice(finalPrice);
		
		rental.setAddressSnapshot(AddressMapper.fromAddress(item.getPickupAdress(), rental));
		rental.setItemSnapshot(ItemMapper.fromItem(item, rental));
		
		return rental;
	}
}
