package br.com.omnirent.item.context;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.user.dto.UserResponseDTO;

public record ItemFeedContext(
		 String id,
		 String name,
		 ItemCondition itemCondition,
		 BigDecimal basePrice,
		 String subCategoryName,
		 Instant createdAt,
		 UserResponseDTO owner
		 ) {}
