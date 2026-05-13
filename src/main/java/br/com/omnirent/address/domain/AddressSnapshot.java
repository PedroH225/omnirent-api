package br.com.omnirent.address.domain;

import br.com.omnirent.rental.domain.Rental;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "address_snapshots")
@NoArgsConstructor
public class AddressSnapshot {	
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Embedded
	private AddressData addressData;
	
	@ToString.Exclude
	@OneToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	public AddressSnapshot(String street, String number,
		    String complement, String district, String city,
		    String state, String country, String zipCode,
		    Rental rental) {
		this.addressData = new AddressData(street, number, complement, district,
				city, state, country, zipCode);
		this.rental = rental;
	}

	
}
