package br.com.omnirent.user.dto;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.user.domain.User;
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

	public UserDetailsDTO(String id, String name, String username, String email, LocalDate birthDate, UserStatus userStatus) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.email = email;
		this.birthDate = dtf.format(birthDate);
		this.userStatus = userStatus.toString();
	}
	 
	public UserDetailsDTO(User user) {
		this.id = user.getId();
		this.name = user.getName();
		this.username = user.getDisplayUsername();
		this.email = user.getEmail();
		this.birthDate = dtf.format(user.getBirthDate());
		this.userStatus = user.getUserStatus().toString();
	}	
}
