package br.com.omnirent.item.dto;

import java.math.BigDecimal;

public record UpdateItemRequestDTO(
		String id,
		String name,
		String model,
		String brand,
		String description,
		BigDecimal basePrice,
		String itemCondition
		) {}
