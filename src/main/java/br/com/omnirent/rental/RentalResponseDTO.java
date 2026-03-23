package br.com.omnirent.rental;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import br.com.omnirent.address.AddressSnapshot;
import br.com.omnirent.address.AddressSnapshotDTO;
import br.com.omnirent.item.ItemSnapshot;
import br.com.omnirent.item.ItemSnapshotDTO;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.UserResponseDTO;
import lombok.Data;

@Data
public class RentalResponseDTO {
	
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

	public RentalResponseDTO(Rental rental) {
		this.id = rental.getId();
		this.startDate = dtf.format(rental.getStartDate());
		this.endDate = dtf.format(rental.getEndDate());
		this.finalPrice = rental.getFinalPrice();
		this.rentalStatus = rental.getRentalStatus().toString();
		this.rentalPeriod = rental.getRentalPeriod().toString();
		
		this.renter = UserMapper.toDto(rental.getRenter());
		this.owner = UserMapper.toDto(rental.getOwner());
		
		this.itemSnapshot = new ItemSnapshotDTO(rental.getItemSnapshot());
		this.addressSnapshot = new AddressSnapshotDTO(rental.getAddressSnapshot());
	}
}
