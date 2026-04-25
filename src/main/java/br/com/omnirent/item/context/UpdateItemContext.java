package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemStatus;

public record UpdateItemContext(
		ItemInfo itemInfo,
		String ownerId,
		ItemStatus status) {}
