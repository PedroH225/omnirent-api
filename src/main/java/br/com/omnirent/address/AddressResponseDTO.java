package br.com.omnirent.address;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	public AddressResponseDTO(Address address) {
	    this.id = address.getId();
	    
	    AddressData addressData = address.getAddressData();
	    
	    this.street = addressData.getStreet();
	    this.number = addressData.getNumber();
	    this.complement = addressData.getComplement();
	    this.district = addressData.getDistrict();
	    this.city = addressData.getCity();
	    this.state = addressData.getState();
	    this.country = addressData.getCountry();
	    this.zipCode = addressData.getZipCode();

	    this.createdAt = dtf.format(address.getCreatedAt());
	    this.updatedAt = dtf.format(address.getUpdatedAt());
	}
	
}

