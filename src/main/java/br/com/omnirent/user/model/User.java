package br.com.omnirent.user.model;

import java.sql.Date;

import br.com.omnirent.common.model.NamedEntity;
import jakarta.persistence.Entity;
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
	
}
