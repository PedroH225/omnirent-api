package br.com.omnirent.rental.dto;

import br.com.omnirent.common.enums.RentalPeriod;

public record RentalRequestDTO(
		String itemId,
		RentalPeriod rentalPeriod
		) {}
