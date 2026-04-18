package br.com.omnirent.factory;

import java.time.LocalDateTime;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressSnapshot;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.domain.User;

public final class RentalTestFactory {

	private RentalTestFactory() {}
	
	public static Rental create(Item item, User owner, User renter, Address address,
			String finalPrice, RentalStatus status, RentalPeriod period) {
		Rental rental = new Rental();
		ItemSnapshot itemSnapshot = new ItemSnapshot(item.getName(), item.getItemData().getBrand(),
				item.getItemData().getModel(), item.getItemData().getDescription(),
				item.getItemData().getBasePrice(), item.getItemData().getItemCondition());
		AddressSnapshot addressSnapshot = new AddressSnapshot(address.getAddressData().getStreet(),
				address.getAddressData().getNumber(), address.getAddressData().getComplement(),
				address.getAddressData().getDistrict(), address.getAddressData().getCity(),
				address.getAddressData().getState(), address.getAddressData().getCountry(),
				address.getAddressData().getZipCode(), rental);
		
		rental.setItemSnapshot(itemSnapshot);
		rental.setAddressSnapshot(addressSnapshot);
		rental.setCreatedAt(LocalDateTime.now());
		rental.setEndDate(LocalDateTime.now());
		rental.setRentalStatus(status);
		rental.setRentalPeriod(period);
		
		return rental;
	}
}
