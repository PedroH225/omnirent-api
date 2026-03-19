package br.com.omnirent.address;

import br.com.omnirent.common.BaseEntity;
import br.com.omnirent.user.User;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "addresses")
public class Address extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	@Embedded
	private AddressData addressData;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	public void addUser(User user) {
		setUser(user);
		user.getAddresses().add(this);
	}
	
	public void updateFields(AddressRequestDTO addressDTO) {
	    this.addressData = new AddressData(addressDTO);
	}

}
