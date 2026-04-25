package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemStatus;

public record ChangeItemSubCategoryContext(
		String id,
		String ownerId,
		String currentSubCategoryId,
		ItemStatus status) {}
