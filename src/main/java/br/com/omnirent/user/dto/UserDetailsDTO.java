package br.com.omnirent.user.dto;

import java.time.LocalDate;

import br.com.omnirent.common.enums.UserStatus;
import lombok.Data;

@Data
public class UserDetailsDTO {
	
	private String id;
	
	private String name;

	private String username;

	private String email;
	
	private LocalDate birthDate;
	
	private UserStatus userStatus;
	
	private String userStatusLabel;

	public UserDetailsDTO(String id, String name, String username, String email, LocalDate birthDate, UserStatus userStatus) {
		this.id = id;
		this.name = name;
		this.username = username;
		this.email = email;
		this.birthDate = birthDate;
		this.userStatus = userStatus;
	}
}
