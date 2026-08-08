package br.com.omnirent.user.context;

public record LoggedUserResponseDTO(
		String id,
		String username,
		String name,
		String locale,
		String timezone
		) {}
