package br.com.omnirent.item.context;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;

public record ItemAuditSnapshot(
        String id,
        String itemName,
        String brand,
        String model,
        String description,
        BigDecimal basePrice,
        ItemCondition itemCondition,
        ItemStatus status,
        String ownerId,
        String categoryId,
        String addressId
) {}
