package br.com.omnirent.address.context;

import lombok.Data;

@Data
public class AddressInfo {
	
	private String id;

	private String street;

	private String number;

	private String complement;
	
	private String district;
	
	private String city;

	private String state;

	private String country;

	private String zipCode;

	public AddressInfo(String id, String street, String number, String complement, String district, String city, String state,
			String country, String zipCode) {
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
}
