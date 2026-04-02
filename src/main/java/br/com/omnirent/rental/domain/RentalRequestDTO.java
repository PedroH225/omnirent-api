package br.com.omnirent.rental.domain;

public record RentalRequestDTO(
		String itemId,
		String rentalPeriod
		) {}
