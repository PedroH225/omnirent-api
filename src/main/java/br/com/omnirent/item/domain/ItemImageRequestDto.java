package br.com.omnirent.item.domain;

import java.util.UUID;

public record ItemImageRequestDto(
		UUID id,
		String tempId,
		String key,
		Integer order) {}
