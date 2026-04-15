package br.com.omnirent.rental.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import br.com.omnirent.address.domain.AddressSnapshot;
import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class RentalDetailDTO {
	
	private String id;
	
	private String startDate;

	private String endDate;
	
	private BigDecimal finalPrice;
	
	private String rentalStatus;
	
	private String rentalPeriod;
	
	private UserResponseDTO renter;
	
	private UserResponseDTO owner;
	
	@JsonProperty("item")
	private ItemSnapshotDTO itemSnapshot;
	
	@JsonProperty("address")
	private AddressSnapshotDTO addressSnapshot;
	
	@JsonIgnore
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
	
	public RentalDetailDTO(String id, LocalDateTime startDate, LocalDateTime endDate, BigDecimal finalPrice, RentalStatus rentalStatus,
			RentalPeriod rentalPeriod, UserResponseDTO renter, UserResponseDTO owner, ItemSnapshotDTO itemSnapshot,
			AddressSnapshotDTO addressSnapshot) {
		this.id = id;
		this.finalPrice = finalPrice;
		this.rentalStatus = rentalStatus.toString();
		this.rentalPeriod = rentalPeriod.toString();
		this.renter = renter;
		this.owner = owner;
		this.itemSnapshot = itemSnapshot;
		this.addressSnapshot = addressSnapshot;
		
		this.startDate = startDate != null ? dtf.format(startDate) : null;
		this.endDate = endDate != null ? dtf.format(endDate) : null;
	}
	
	public RentalDetailDTO(Rental rental) {
		this.id = rental.getId();
		
		this.finalPrice = rental.getFinalPrice();
		this.rentalStatus = rental.getRentalStatus().toString();
		this.rentalPeriod = rental.getRentalPeriod().toString();
		
		//this.renter = UserMapper.toDto(rental.getRenter());
		//this.owner = UserMapper.toDto(rental.getOwner());
		
		this.itemSnapshot = new ItemSnapshotDTO(rental.getItemSnapshot());
		this.addressSnapshot = new AddressSnapshotDTO(rental.getAddressSnapshot());
		
		if (rental.getStartDate() != null) {
			this.startDate = dtf.format(rental.getStartDate());
		}
		
		if (rental.getStartDate() != null) {
			this.endDate = dtf.format(rental.getEndDate());
		}
	}

	
	
	
}
