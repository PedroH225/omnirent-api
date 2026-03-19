package br.com.omnirent.address;

import br.com.omnirent.rental.Rental;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "address_snapshots")
public class AddressSnapshot {

	@Id
	private String rentalId;
	
	private String street;

	private String number;

	private String complement;
	
	private String district;
	
	private String city;

	private String state;

	private String country;

	private String zipCode;
	
	@MapsId
	@OneToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	public AddressSnapshot(Address address) {
		this.street = address.getStreet();
		this.number = address.getNumber();
		this.complement = address.getComplement();
		this.district = address.getDistrict();
		this.city = address.getCity();
		this.state = address.getState();
		this.country = address.getCountry();
		this.zipCode = address.getZipCode();
	}
	
	
}
