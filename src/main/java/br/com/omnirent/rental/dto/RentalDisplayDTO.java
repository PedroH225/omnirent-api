package br.com.omnirent.rental.dto;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
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
	
	private String itemId;
	
	private String itemName;
		
	private String renterId;
	
	private String renterName;
	
	private String ownerId;
	
	private String ownerName;
	
	private Instant createdAt;

	public RentalDisplayDTO(String id, Instant startDate, Instant endDate, BigDecimal finalPrice, RentalStatus rentalStatus,
			RentalPeriod rentalPeriod, String itemId, String itemName, String renterId, String renterName, String ownerId,
			String ownerName, Instant createdAt) {
		this.id = id;
		this.finalPrice = finalPrice;
		this.rentalStatus = rentalStatus;
		this.rentalPeriod = rentalPeriod;
		this.itemId = itemId;
		this.itemName = itemName;
		this.renterId = renterId;
		this.renterName = renterName;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		
		this.createdAt = createdAt;
		this.startDate = startDate;
		this.endDate = endDate; 
	}
	
	
}
