package br.com.omnirent.item.context;

import java.util.UUID;

public record ItemImageResponseDTO(
		UUID id,
		String storageKey,
		Integer displayOrder) {}
