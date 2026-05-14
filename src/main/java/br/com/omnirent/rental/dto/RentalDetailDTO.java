package br.com.omnirent.rental.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class RentalDetailDTO {
	
	private String id;
	
	private String startDate;

	private String endDate;
	
	private BigDecimal finalPrice;
	
	private RentalStatus rentalStatus;
	
	private String rentalStatusLabel;

	private RentalPeriod rentalPeriod;
	
	private String rentalPeriodLabel;

	private UserResponseDTO renter;
	
	private UserResponseDTO owner;
	
	@JsonProperty("item")
	private ItemSnapshotDTO itemSnapshot;
	
	@JsonProperty("address")
	private AddressSnapshotDTO addressSnapshot;
	
	@JsonIgnore
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	
	public RentalDetailDTO(String id, LocalDateTime startDate, LocalDateTime endDate, BigDecimal finalPrice, RentalStatus rentalStatus,
			RentalPeriod rentalPeriod, UserResponseDTO renter, UserResponseDTO owner, ItemSnapshotDTO itemSnapshot,
			AddressSnapshotDTO addressSnapshot) {
		this.id = id;
		this.finalPrice = finalPrice;
		this.rentalStatus = rentalStatus;
		this.rentalPeriod = rentalPeriod;
		this.renter = renter;
		this.owner = owner;
		this.itemSnapshot = itemSnapshot;
		this.addressSnapshot = addressSnapshot;
		
		this.startDate = startDate != null ? dtf.format(startDate) : null;
		this.endDate = endDate != null ? dtf.format(endDate) : null;
	}
}
