package br.com.omnirent.address.domain;

import jakarta.persistence.Embedded;
import br.com.omnirent.rental.domain.Rental;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@Embedded
	private AddressData addressData;
	
	@OneToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	public AddressSnapshot(Address address) {
		this.addressData = address.getAddressData();
	}
}
