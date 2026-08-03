package br.com.omnirent.rental.dto;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.dto.ItemSnapshotDto;
import lombok.Data;

@Data
public class RentalDisplayDTO {

	private String id;
	
	private Instant startDate;

	private Instant endDate;
	
	private BigDecimal finalPrice;
	
	private RentalStatus rentalStatus;
	
	private String rentalStatusLabel;
	
	private RentalPeriod rentalPeriod;
	
	private String rentalPeriodLabel;
	
	private ItemSnapshotDto itemSnapshotDto;
	
	private Instant createdAt;

	public RentalDisplayDTO(String id, Instant startDate, Instant endDate, BigDecimal finalPrice, RentalStatus rentalStatus,
			RentalPeriod rentalPeriod, ItemSnapshotDto itemSnapshotDto, Instant createdAt) {
		this.id = id;
		this.finalPrice = finalPrice;
		this.rentalStatus = rentalStatus;
		this.rentalPeriod = rentalPeriod;
		this.itemSnapshotDto = itemSnapshotDto;
		
		this.createdAt = createdAt;
		this.startDate = startDate;
		this.endDate = endDate; 
	}
	
	
}
