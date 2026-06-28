package br.com.omnirent.rental.dto;

import java.math.BigDecimal;
import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import lombok.Data;

@Data
public class RentalCreatedDTO {
private String id;
	
	private Instant startDate;

	private Instant endDate;
	
	private BigDecimal finalPrice;
	
	private RentalStatus rentalStatus;
	
	private String rentalStatusLabel;
	
	private RentalPeriod rentalPeriod;
	
	private String rentalPeriodLabel;
			
	@JsonProperty("item")
	private ItemSnapshotDTO itemSnapshot;
	
	@JsonProperty("address")
	private AddressSnapshotDTO addressSnapshot;
	
	public RentalCreatedDTO(String id, Instant startDate, Instant endDate, BigDecimal finalPrice, RentalStatus rentalStatus,
			RentalPeriod rentalPeriod, ItemSnapshotDTO itemSnapshot,
			AddressSnapshotDTO addressSnapshot) {
		this.id = id;
		this.finalPrice = finalPrice;
		this.rentalStatus = rentalStatus;
		this.rentalPeriod = rentalPeriod;
		this.itemSnapshot = itemSnapshot;
		this.addressSnapshot = addressSnapshot;
		
		this.startDate = startDate;
		this.endDate = endDate;
	}
}
