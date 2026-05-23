 package br.com.omnirent.rental;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.omnirent.address.AddressMapper;
import br.com.omnirent.address.context.AddressInfo;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.common.enums.EnumOption;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.common.enums.RentalEnums;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.item.ItemMapper;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RentalMapper {
	
	private ItemMapper itemMapper;
	
	private AddressMapper addressMapper;
	
	private MessageService messageService;
	
	public RentalCreatedDTO toCreatedDto(Rental rental) {
		ItemSnapshotDTO itemSnapshotDTO = itemMapper.toSnapshotDTO(rental.getItemSnapshot());
		AddressSnapshotDTO addressSnapshotDTO = addressMapper.toSnapDto(rental.getAddressSnapshot());
		RentalCreatedDTO newRental = new RentalCreatedDTO(
			    rental.getId(), rental.getStartDate(), rental.getEndDate(),
			    rental.getFinalPrice(), rental.getRentalStatus(), rental.getRentalPeriod(),
			    itemSnapshotDTO, addressSnapshotDTO
			);
		
		newRental.setRentalPeriodLabel(messageService.get(newRental.getRentalPeriod().getMessageKey()));
		newRental.setRentalStatusLabel(messageService.get(newRental.getRentalStatus().getMessageKey()));
		
		return newRental;
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
	
	public List<RentalDisplayDTO> localize(List<RentalDisplayDTO> displayDTOs) {
		displayDTOs.forEach(r -> {
		r.setRentalPeriodLabel(messageService.get(r.getRentalPeriod().getMessageKey()));
		r.setRentalStatusLabel(messageService.get(r.getRentalStatus().getMessageKey()));
		});
		
		return displayDTOs;
	}
	
	public RentalDisplayDTO localize(RentalDisplayDTO displayDTO) {
		displayDTO.setRentalPeriodLabel(messageService.get(displayDTO.getRentalPeriod().getMessageKey()));
		displayDTO.setRentalStatusLabel(messageService.get(displayDTO.getRentalStatus().getMessageKey()));
	
		return displayDTO;
	}
	
	public RentalDetailDTO localize(RentalDetailDTO detailDTO) {
		detailDTO.setRentalPeriodLabel(messageService.get(detailDTO.getRentalPeriod().getMessageKey()));
		detailDTO.setRentalStatusLabel(messageService.get(detailDTO.getRentalStatus().getMessageKey()));
		
		ItemSnapshotDTO itemSnapshotDTO = detailDTO.getItemSnapshot();
		itemSnapshotDTO.setItemConditionLabel(
				messageService.get(itemSnapshotDTO.getItemCondition().getMessageKey()));
		return detailDTO;
	}

	public RentalEnums getLocalizedEnums() {
		List<EnumOption> rentalPeriods = Arrays.stream(RentalPeriod.values())
				.map(i -> new EnumOption(i.name(), messageService.get(i.getMessageKey())))
				.collect(Collectors.toList());
		
		List<EnumOption> rentalStatuses = Arrays.stream(RentalStatus.values())
				.map(i -> new EnumOption(i.name(), messageService.get(i.getMessageKey())))
				.collect(Collectors.toList());
		
		return new RentalEnums(rentalPeriods, rentalStatuses);
	}
}
