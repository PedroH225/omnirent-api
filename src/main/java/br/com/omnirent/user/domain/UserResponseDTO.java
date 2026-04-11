package br.com.omnirent.user.domain;

import lombok.Data;

@Data
public class UserResponseDTO {

	public String id;
	
	public String username;

	public UserResponseDTO(User user) {
		this.id = user.getId();
		this.username = user.getDisplayUsername();
	}
	
	
}
