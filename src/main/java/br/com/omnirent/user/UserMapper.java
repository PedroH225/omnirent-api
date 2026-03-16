package br.com.omnirent.user;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
	public static List<UserResponseDTO> toDto(List<User> users) {
		return users.stream()
				.map(u -> toDto(u))
				.collect(Collectors.toList());
	}
	
	public static UserResponseDTO toDto(User user) {
		UserResponseDTO userDTO = new UserResponseDTO(user);
		return userDTO;
	}
}
