package br.com.omnirent.item;

import java.math.BigDecimal;

public record ItemRequestDTO(
		String name,
		String model,
		String brand,
		String description,
		BigDecimal basePrice,
		String itemCondition,
		String subCategoryId,
		String addressId
		) {}
