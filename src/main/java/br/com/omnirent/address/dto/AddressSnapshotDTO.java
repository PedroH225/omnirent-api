package br.com.omnirent.address.dto;

import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.address.domain.AddressSnapshot;
import lombok.Data;

@Data
public class AddressSnapshotDTO {
	
	private String id;
	
	private String street;

	private String number;

	private String complement;
	
	private String district;
	
	private String city;

	private String state;

	private String country;

	private String zipCode;
	
	public AddressSnapshotDTO(String id, String street, String number, String complement, String district, String city,
			String state, String country, String zipCode) {
		this.id = id;
		this.street = street;
		this.number = number;
		this.complement = complement;
		this.district = district;
		this.city = city;
		this.state = state;
		this.country = country;
		this.zipCode = zipCode;
	}

	public AddressSnapshotDTO(AddressSnapshot address) {
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
	}
}
