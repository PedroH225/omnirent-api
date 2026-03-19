package br.com.omnirent.address;

import br.com.omnirent.rental.Rental;
import jakarta.persistence.Embedded;
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
	
	@Embedded
	private AddressData addressData;
	
	@MapsId
	@OneToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	public AddressSnapshot(Address address) {
		this.addressData = address.getAddressData();
	}
}
