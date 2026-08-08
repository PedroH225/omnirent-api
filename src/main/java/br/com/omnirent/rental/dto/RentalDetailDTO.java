package br.com.omnirent.rental.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.dto.ItemDetailSnapshotDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class RentalDetailDTO {
	
	private String id;
	
	private Instant startDate;

	private Instant endDate;
	
	private BigDecimal finalPrice;
	
	private RentalStatus rentalStatus;
	
	private String rentalStatusLabel;

	private RentalPeriod rentalPeriod;
	
	private String rentalPeriodLabel;
	
	private UserResponseDTO renter;
	
	private UserResponseDTO owner;
	
	@JsonProperty("item")
	private ItemDetailSnapshotDTO itemSnapshot;
	
	@JsonProperty("address")
	private AddressSnapshotDTO addressSnapshot;
		
	public RentalDetailDTO(String id, Instant startDate, Instant endDate, BigDecimal finalPrice, RentalStatus rentalStatus,
			RentalPeriod rentalPeriod, UserResponseDTO renter, UserResponseDTO owner,
			ItemDetailSnapshotDTO itemSnapshot, AddressSnapshotDTO addressSnapshot) {
		this.id = id;
		this.finalPrice = finalPrice;
		this.rentalStatus = rentalStatus;
		this.rentalPeriod = rentalPeriod;
		this.renter = renter;
		this.owner = owner;
		this.itemSnapshot = itemSnapshot;
		this.addressSnapshot = addressSnapshot;
		
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
