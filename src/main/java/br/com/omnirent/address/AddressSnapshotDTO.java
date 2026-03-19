package br.com.omnirent.address;

import lombok.Data;

@Data
public class AddressSnapshotDTO {
	
	private String rentalId;
	
	private String street;

	private String number;

	private String complement;
	
	private String district;
	
	private String city;

	private String state;

	private String country;

	private String zipCode;

	public AddressSnapshotDTO(AddressSnapshot address) {
		this.rentalId = address.getRentalId();
		
		AddressData addressData = address.getAddressData();
		
		this.street = addressData.getStreet();
		this.number = addressData.getNumber();
		this.complement = addressData.getComplement();
		this.district = addressData.getDistrict();
		this.city = addressData.getCity();
		this.state = addressData.getState();
		this.country = addressData.getCountry();
		this.zipCode = addressData.getZipCode();
	}
}
