package br.com.omnirent.address.dto;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
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
	
	private Instant createdAt;
	
	private Instant updatedAt;

	public AddressResponseDTO(String id, String street, String number, String complement, String district, String city,
			String state, String country, String zipCode, Instant createdAt, Instant updatedAt) {
		this.id = id;
		this.street = street;
		this.number = number;
		this.complement = complement;
		this.district = district;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipCode = zipCode;
		
	    this.createdAt = createdAt;
	    this.updatedAt = updatedAt;
	}
	
}

