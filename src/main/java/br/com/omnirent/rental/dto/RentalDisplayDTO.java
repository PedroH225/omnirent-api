package br.com.omnirent.rental.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import lombok.Data;

@Data
public class RentalDisplayDTO {

	private String id;
	
	private String startDate;

	private String endDate;
	
	private BigDecimal finalPrice;
	
	private String rentalStatus;
	
	private String rentalPeriod;
	
	private String itemId;
	
	private String itemName;
		
	private String renterId;
	
	private String renterName;
	
	private String ownerId;
	
	private String ownerName;
	
	private String createdAt;
	
	@JsonIgnore
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public RentalDisplayDTO(String id, LocalDateTime startDate, LocalDateTime endDate, BigDecimal finalPrice, RentalStatus rentalStatus,
			RentalPeriod rentalPeriod, String itemId, String itemName, String renterId, String renterName, String ownerId,
			String ownerName, LocalDateTime createdAt) {
		this.id = id;
		this.finalPrice = finalPrice;
		this.rentalStatus = rentalStatus.toString();
		this.rentalPeriod = rentalPeriod.toString();
		this.itemId = itemId;
		this.itemName = itemName;
		this.renterId = renterId;
		this.renterName = renterName;
		this.ownerId = ownerId;
		this.ownerName = ownerName;
		
		this.createdAt = dtf.format(createdAt);
		this.startDate = startDate != null ? dtf.format(startDate) : null;
		this.endDate = endDate != null ? dtf.format(endDate) : null; 
	}
	
	
}
