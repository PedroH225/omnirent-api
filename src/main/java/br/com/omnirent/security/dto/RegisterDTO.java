package br.com.omnirent.security.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record RegisterDTO(
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
		LocalDate birthDate,
		
		@NotBlank(message = "required")
		String password,
		
		@NotBlank(message = "required")
		String repeatedPassword
		
		) {}
