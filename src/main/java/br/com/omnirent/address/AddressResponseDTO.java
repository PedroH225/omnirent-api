package br.com.omnirent.address;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class AddressResponseDTO {
	private static final long serialVersionUID = 1L;
	
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
	
	public AddressResponseDTO(Address address) {
	    this.id = address.getId();
	    this.street = address.getStreet();
	    this.number = address.getNumber();
	    this.complement = address.getComplement();
	    this.district = address.getDistrict();
	    this.city = address.getCity();
	    this.state = address.getState();
	    this.country = address.getCountry();
	    this.zipCode = address.getZipCode();

	    this.createdAt = dtf.format(address.getCreatedAt());
	    this.updatedAt = dtf.format(address.getUpdatedAt());
	}
	
}

