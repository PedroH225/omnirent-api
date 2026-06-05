package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemStatus;

public record UpdateItemContext(
		ItemInfo itemInfo,
		String ownerId,
		String addressId,
		String subCategoryId,
		ItemStatus status) {}
