 package br.com.omnirent.rental;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.omnirent.address.AddressMapper;
import br.com.omnirent.address.context.AddressInfo;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.ItemMapper;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RentalMapper {
	
	private ItemMapper itemMapper;
	
	private AddressMapper addressMapper;
	
	private UserMapper userMapper;

	public RentalDetailDTO toDetailDto(Rental rental) {
		return new RentalDetailDTO(rental);
	}
	
	public List<RentalDetailDTO> toDto(List<Rental> rentals) {
		return rentals.stream()
				.map(RentalDetailDTO::new)
				.collect(Collectors.toList());
	}
	
	public RentalCreatedDTO toCreatedDto(Rental rental) {
		ItemSnapshotDTO itemSnapshotDTO = itemMapper.toSnapshotDTO(rental.getItemSnapshot());
		AddressSnapshotDTO addressSnapshotDTO = addressMapper.toSnapDto(rental.getAddressSnapshot());
		return new RentalCreatedDTO(
			    rental.getId(), rental.getStartDate(), rental.getEndDate(),
			    rental.getFinalPrice(), rental.getRentalStatus(), rental.getRentalPeriod(),
			    itemSnapshotDTO, addressSnapshotDTO
			);
	}
	
	public Rental create(User renter, String renterId, ItemRentedContext context,
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
		
		rental.setAddressSnapshot(addressMapper.fromRentContext(addressInfo, rental));
		rental.setItemSnapshot(itemMapper.fromRentContext(item, rental));
		
		return rental;
	}
	
	public static Rental setDates(Rental rental, LocalDateTime startDate, LocalDateTime endDate) {
		rental.setStartDate(startDate);
		rental.setEndDate(endDate);
		
		return rental;
	}
}
