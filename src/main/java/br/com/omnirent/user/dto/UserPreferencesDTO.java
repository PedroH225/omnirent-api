package br.com.omnirent.user.dto;

public record UserPreferencesDTO(
		String locale,
		String timezone
	) {}
