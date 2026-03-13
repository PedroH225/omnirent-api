package br.com.omnirent.adress.model;

import br.com.omnirent.common.model.BaseEntity;
import br.com.omnirent.user.model.User;
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
	
	private String street;

	private String number;

	private String complement;
	
	private String district;
	
	private String city;

	private String state;

	private String country;

	private String zipCode;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

}
