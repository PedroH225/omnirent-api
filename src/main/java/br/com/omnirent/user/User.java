package br.com.omnirent.user;

import java.sql.Date;
import java.util.List;

import br.com.omnirent.address.Address;
import br.com.omnirent.common.NamedEntity;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.item.Item;
import br.com.omnirent.rental.Rental;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "users")
public class User extends NamedEntity {
	private static final long serialVersionUID = 1L;
	
	private String username;

	private String email;

	private String password;
	
	private Date birthDate;
	
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;
	
	@OneToMany(mappedBy = "user")
	private List<Address> adresses;
	
	@OneToMany(mappedBy = "owner")
	private List<Item> items;
	
	@OneToMany(mappedBy = "renter")
	private List<Rental> rented;
	
}
