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
public class RentalCreatedDTO {
private String id;
	
	private String startDate;

	private String endDate;
	
	private BigDecimal finalPrice;
	
	private String rentalStatus;
	
	private String rentalPeriod;
			
	@JsonProperty("item")
	private ItemSnapshotDTO itemSnapshot;
	
	@JsonProperty("address")
	private AddressSnapshotDTO addressSnapshot;
	
	@JsonIgnore
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
	
	public RentalCreatedDTO(String id, LocalDateTime startDate, LocalDateTime endDate, BigDecimal finalPrice, RentalStatus rentalStatus,
			RentalPeriod rentalPeriod, ItemSnapshotDTO itemSnapshot,
			AddressSnapshotDTO addressSnapshot) {
		this.id = id;
		this.finalPrice = finalPrice;
		this.rentalStatus = rentalStatus.toString();
		this.rentalPeriod = rentalPeriod.toString();
		this.itemSnapshot = itemSnapshot;
		this.addressSnapshot = addressSnapshot;
		
		this.startDate = startDate != null ? dtf.format(startDate) : null;
		this.endDate = endDate != null ? dtf.format(endDate) : null;
	}
}
