package br.com.omnirent.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.domain.UserDetailsDTO;
import br.com.omnirent.user.domain.UserResponseDTO;

@Component
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
	
	public UserDetailsDTO toDetailsDto(User user) {
		UserDetailsDTO userDTO = new UserDetailsDTO(user);
		return userDTO;
	}
}
