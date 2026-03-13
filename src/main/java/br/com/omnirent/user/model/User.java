package br.com.omnirent.user.model;

import java.sql.Date;

import br.com.omnirent.common.model.NamedEntity;
import br.com.omnirent.user.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
}
