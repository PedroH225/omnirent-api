package br.com.omnirent.user.domain;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.common.NamedEntity;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.security.domain.ExternalIdentity;
import br.com.omnirent.security.domain.Role;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends NamedEntity {
	private static final long serialVersionUID = 1L;
	
	private String username;

	private String email;

	private String password;
	
	private LocalDate birthDate;
		
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;
	
	private String locale;
	
	private String timezone;
	
	@OneToMany(mappedBy = "user")
	private List<Address> addresses;
	
	@OneToMany(mappedBy = "owner")
	private List<Item> items;
	
	@OneToMany(mappedBy = "renter")
	private List<Rental> rented;
	
	@OneToMany(mappedBy = "owner")
	private List<Rental> rentals;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "user_roles",
	    joinColumns = @JoinColumn(name = "user_id"),
	    inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	
	@Embedded
	private AuthMetadata authMetadata;
	
	@OneToMany(mappedBy = "user")
	private Set<ExternalIdentity> authProvider;
	
	public User(String id, String name, String username, String email, String password, LocalDate birthDate, Integer tokenVersion, Integer globalVersion) {
		this.id = id;
		this.name = name;
		this.username =  username;
		this.email = email;
		this.password = password;
		this.birthDate = birthDate;
		
		AuthMetadata authMetadata = new AuthMetadata();
        authMetadata.setTokenVersion(tokenVersion);
        authMetadata.setGlobalVersion(globalVersion);
                
        this.authMetadata = authMetadata;
	}
	
	@PrePersist
	public void onCreate() {
		setUserStatus(UserStatus.ACTIVE);
	}

}
