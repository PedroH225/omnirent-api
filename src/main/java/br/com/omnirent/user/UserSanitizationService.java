package br.com.omnirent.user;

import org.springframework.stereotype.Component;

import br.com.omnirent.security.dto.RegisterDTO;
import br.com.omnirent.user.dto.UserRequestDTO;

@Component
public class UserSanitizationService {

	public RegisterDTO sanitizeFields(RegisterDTO registerDTO) {
		return new RegisterDTO(
				sanitizeName(registerDTO.name()), 
				sanitizeUsername(registerDTO.username()),
				sanitizeEmail(registerDTO.email()),
				registerDTO.birthDate(), registerDTO.password(), registerDTO.repeatedPassword());
	}
	
	public UserRequestDTO sanitizeFields(UserRequestDTO registerDTO) {
		return new UserRequestDTO(
				sanitizeName(registerDTO.name()), 
				sanitizeUsername(registerDTO.username()),
				sanitizeEmail(registerDTO.email()),
				registerDTO.birthDate());
	}
	
	private String sanitizeName(String name) {
		return name != null ?
				name.strip()
				.replaceAll("\\s+", " ")
				: null;
	}
	
	private String sanitizeUsername(String username) {
		return username != null ? 
				username.strip()
				.replaceAll("\\s+", "")
				.toLowerCase() 
				: null;
	}
		
	private String sanitizeEmail(String email) {
		return email != null ?
				email.strip()
				.replaceAll("\\s+", "")
				.toLowerCase()
				: null;
	}
}
