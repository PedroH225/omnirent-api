package br.com.omnirent.factory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.omnirent.address.context.AddressInfo;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressSnapshot;
import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.rental.dto.RentalRequestDTO;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.Sequence;

public final class RentalTestFactory {

	private RentalTestFactory() {}
	
	public static Rental create(Item item, User owner, User renter, Address address,
			String finalPrice, RentalStatus status, RentalPeriod period, LocalDateTime startDate, LocalDateTime endDate) {
		Rental rental = new Rental();
		ItemSnapshot itemSnapshot = new ItemSnapshot(item.getName(), item.getItemData().getBrand(),
				item.getItemData().getModel(), item.getItemData().getDescription(),
				item.getItemData().getBasePrice(), item.getItemData().getItemCondition());
		itemSnapshot.setRental(rental);
		AddressSnapshot addressSnapshot = new AddressSnapshot(address.getAddressData().getStreet(),
				address.getAddressData().getNumber(), address.getAddressData().getComplement(),
				address.getAddressData().getDistrict(), address.getAddressData().getCity(),
				address.getAddressData().getState(), address.getAddressData().getCountry(),
				address.getAddressData().getZipCode(), rental);
		
		rental.setItemSnapshot(itemSnapshot);
		rental.setAddressSnapshot(addressSnapshot);
		rental.setStartDate(startDate);
		rental.setEndDate(endDate);
		rental.setRentalStatus(status);
		rental.setRentalPeriod(period);
		rental.setFinalPrice(new BigDecimal(finalPrice));
		
		rental.setOwner(owner);
		rental.setOwnerId(owner.getId());
		
		rental.setRenter(renter);
		rental.setRenterId(renter.getId());
		
		return rental;
	}
	
	public static Rental create(User renter, String renterId, ItemRentedContext context,
		    RentalPeriod rentalPeriod, RentalStatus rentalStatus,
		    BigDecimal finalPrice) {
		Rental rental = new Rental();
		AddressInfo addressInfo = context.getAddressInfo();
		ItemInfo item = context.getItemInfo();
		
		rental.setOwnerId(context.getOwnerId());
		rental.assignRenter(renter, renterId);
		
		rental.setRentalPeriod(rentalPeriod);
		
		rental.setRentalStatus(rentalStatus);
		
		rental.setFinalPrice(finalPrice);
		
		rental.setAddressSnapshot(AddressTestFactory.toSnapshot(addressInfo, rental));
		
		rental.setItemSnapshot(ItemTestFactory.toSnapshot(item, rental));
		
		return rental;
	}
	
	public static Rental createPersisted(Item item, User owner, User renter, Address address,
			String finalPrice, RentalStatus status, RentalPeriod period, LocalDateTime startDate, LocalDateTime endDate) {
		Rental rental = create(item, owner, renter, address, finalPrice, status, period, startDate, endDate);
		
		rental.setId(Sequence.nextString("rentalId"));
		rental.setCreatedAt(LocalDateTime.now());
		rental.setUpdatedAt(LocalDateTime.now());
		
		return rental;
	}
	
	public static Rental toPersisted(Rental rental) {
		rental.setId(Sequence.nextString("rentalId"));
		rental.setCreatedAt(LocalDateTime.now());
		rental.setUpdatedAt(LocalDateTime.now());
		
		return rental;
	}
	
	public static RentalCreatedDTO toCreatedDTO(Rental rental) {
		ItemSnapshotDTO itemSnapshotDTO = ItemTestFactory.toSnapshotDTO(rental.getItemSnapshot());
		AddressSnapshotDTO addressSnapshotDTO = AddressTestFactory.toSnapDto(rental.getAddressSnapshot());
		
		return new RentalCreatedDTO(
			    rental.getId(), rental.getStartDate(), rental.getEndDate(),
			    rental.getFinalPrice(), rental.getRentalStatus(), rental.getRentalPeriod(),
			    itemSnapshotDTO, addressSnapshotDTO
			);
	}
	
	public static RentalDisplayDTO toRentalDisplayDTO(Rental rental) {
	    return new RentalDisplayDTO(
	        rental.getId(), rental.getStartDate(), rental.getEndDate(),
	        rental.getFinalPrice(), rental.getRentalStatus(), rental.getRentalPeriod(),
	        rental.getItemSnapshot().getId(), rental.getItemSnapshot().getName(), rental.getRenterId(),
	        rental.getRenter().getName(), rental.getOwnerId(), rental.getOwner().getName(),
	        rental.getCreatedAt()
	    );
	}
	
	public static RentalRequestDTO newRentalRequest(String itemId, String rentalPeriod) {
		return new RentalRequestDTO(itemId, rentalPeriod);
	}
}
