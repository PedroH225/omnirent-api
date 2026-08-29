package br.com.omnirent.rental.dto;

import java.time.Instant;

import br.com.omnirent.common.enums.RentalStatus;

public record RentalOperationDTO(
		RentalStatus status,
		Instant startDate,
		Instant endDate,
		Instant updatedAt
	) {}
