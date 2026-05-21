package br.com.omnirent.item.dto;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;

public record ItemRequestDTO(
		String id,
		String name,
		String model,
		String brand,
		String description,
		BigDecimal basePrice,
		ItemCondition itemCondition,
		String subCategoryId,
		String addressId
		) {}
