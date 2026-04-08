package br.com.omnirent.item.domain;

import java.math.BigDecimal;

public record ItemRequestDTO(
		String id,
		String name,
		String model,
		String brand,
		String description,
		BigDecimal basePrice,
		String itemCondition,
		String subCategoryId,
		String addressId
		) {}
