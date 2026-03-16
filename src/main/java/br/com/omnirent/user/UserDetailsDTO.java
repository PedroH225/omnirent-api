package br.com.omnirent.user;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class UserDetailsDTO {
	
	private String id;
	
	private String name;

	private String username;

	private String email;
	
	private String birthDate;
	
	private String userStatus;
	
	@JsonIgnore
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public UserDetailsDTO(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.username = user.getDisplayUsername();
		this.email = user.getEmail();
		this.birthDate = dtf.format(user.getBirthDate());
		this.userStatus = user.getUserStatus().toString();
	}
	
	
	
}
