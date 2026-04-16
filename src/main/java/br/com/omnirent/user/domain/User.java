package br.com.omnirent.user.domain;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.common.NamedEntity;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.dto.UserRequestDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class User extends NamedEntity implements UserDetails {
	private static final long serialVersionUID = 1L;
	
	private String username;

	private String email;

	private String password;
	
	private LocalDate birthDate;
		
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;
	
	@OneToMany(mappedBy = "user")
	private List<Address> addresses;
	
	@OneToMany(mappedBy = "owner")
	private List<Item> items;
	
	@OneToMany(mappedBy = "renter")
	private List<Rental> rented;
	
	@OneToMany(mappedBy = "owner")
	private List<Rental> rentals;
	
	@Embedded
	private AuthMetadata authMetadata;
	
	public User update(UserRequestDTO userDTO) {
		this.name = userDTO.name();
		this.username = userDTO.username();
		this.email = userDTO.email();
		this.birthDate = userDTO.birthDate();
		
		return this;
	}
	
	public User(String id, String email, String password, Integer tokenVersion, Integer globalVersion) {
		this.id = id;
		this.email = email;
		this.password = password;
		
		AuthMetadata authMetadata = new AuthMetadata();
        authMetadata.setTokenVersion(tokenVersion);
        authMetadata.setGlobalVersion(globalVersion);
        
        this.authMetadata = authMetadata;
	}
	
	public User deactivate() {
		if (this.userStatus == UserStatus.BANNED) {
			throw new RuntimeException("User is banned.");
		}
		
		this.userStatus = UserStatus.INACTIVE;
		return this;
	}
	
	public User activate() {
		if (this.userStatus == UserStatus.BANNED) {
			throw new RuntimeException("User is banned.");
		}
		
		this.userStatus = UserStatus.ACTIVE;
		return this;
	}
	
	@PrePersist
	public void onCreate() {
		setUserStatus(UserStatus.ACTIVE);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return Collections.emptyList();
	}
	
	@Override
	public String getPassword() {
		return password;
	}


	@Override
	public String getUsername() {
		return email;
	}
	
	public String getDisplayUsername() {
		return username;
	}
	
	@Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;

    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }

	public User(String name, String username, String email, String password, LocalDate birthDate, Integer tokenVersion, Integer globalVersion) {
		this.name = name;
		this.username = username;
		this.email = email;
		this.password = password;
		this.birthDate = birthDate;
		
		AuthMetadata authMetadata = new AuthMetadata();
		authMetadata.setGlobalVersion(globalVersion);
		authMetadata.setTokenVersion(tokenVersion);
		this.authMetadata = authMetadata;
	}
    
    
}
