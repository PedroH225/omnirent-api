package br.com.omnirent.address;

import br.com.omnirent.common.BaseEntity;
import br.com.omnirent.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
	
	@Column(name = "user_id")
	private String userId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;
	
	public void assignUser(User user) {
		this.user = user;
		this.userId = user.getId();
	}
	
	public void updateFields(AddressRequestDTO addressDTO) {
	    this.addressData = new AddressData(addressDTO);
	}

}
