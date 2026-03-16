package br.com.omnirent.user;

import java.time.LocalDate;

public record UserRequestDTO(
	String name, 
	
	String username,
	
	String email,
	
	LocalDate birthDate
) {}
