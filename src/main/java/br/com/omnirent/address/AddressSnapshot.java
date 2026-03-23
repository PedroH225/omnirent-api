package br.com.omnirent.address;

import br.com.omnirent.rental.Rental;
import jakarta.persistence.Embedded;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "address_snapshots")
@NoArgsConstructor
public class AddressSnapshot {	
	
	@Id
	private String rentalId;
	
	@Embedded
	private AddressData addressData;
	
	@MapsId("rentalId")
	@OneToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	public AddressSnapshot(Address address) {
		this.addressData = address.getAddressData();
	}
}
