package br.com.omnirent.user.dto;

import java.time.LocalDate;

import br.com.omnirent.user.domain.UserIdentityInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
		@NotBlank(message = "required")
		@Size(min=5, max = 100, message = "size")
		String name, 
		
		@NotBlank(message = "required")
		String username,
		
		@NotBlank(message = "required")
		@Email(message = "invalid_email")
		String email,
		
		@NotNull(message = "required")
		@Past(message = "past")
		LocalDate birthDate
) implements UserIdentityInput {

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getEmail() {
		return email;
	}}
