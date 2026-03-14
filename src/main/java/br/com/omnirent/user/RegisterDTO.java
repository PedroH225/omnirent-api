package br.com.omnirent.user;

import java.time.LocalDate;

public record RegisterDTO(
		String name, 
		
		String username,
		
		String email,
		
		LocalDate birthDate,
		
		String password,
		
		String repeatedPassword
		
		) {
    
}
