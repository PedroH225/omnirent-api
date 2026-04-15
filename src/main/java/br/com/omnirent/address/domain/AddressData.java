package br.com.omnirent.address.domain;

import br.com.omnirent.address.dto.AddressRequestDTO;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class AddressData {
	
	private String street;

	private String number;

	private String complement;
	
	private String district;
	
	private String city;

	private String state;

	private String country;

	private String zipCode;
	
    public AddressData(AddressRequestDTO address) {
        this.street = address.street();
        this.number = address.number();
        this.complement = address.complement();
        this.district = address.district();
        this.city = address.city();
        this.state = address.state();
        this.country = address.country();
        this.zipCode = address.zipCode();
    }

	public AddressData(String street, String number, String complement, String district, String city, String state,
			String country, String zipCode) {
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
