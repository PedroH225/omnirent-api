package br.com.omnirent.address.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import lombok.Data;

@Data
public class AddressResponseDTO {
	
	private String id;
	
	private String street;

	private String number;

	private String complement;
	
	private String district;
	
	private String city;

	private String state;

	private String country;

	private String zipCode;
	
	private String createdAt;
	
	private String updatedAt;
	
	@JsonIgnore
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public AddressResponseDTO(String id, String street, String number, String complement, String district, String city,
			String state, String country, String zipCode, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.street = street;
		this.number = number;
		this.complement = complement;
		this.district = district;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipCode = zipCode;
		
	    this.createdAt = dtf.format(createdAt);
	    this.updatedAt = dtf.format(updatedAt);
	}
	
}

