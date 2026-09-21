package br.com.omnirent.item.dto;

import java.time.Instant;

public record LastUpdateDto(
		Instant updatedAt
	) {}
