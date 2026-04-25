package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemStatus;

public record UpdateItemStatusContext(
		String id,
		ItemStatus currentStatus,
		String ownerId
		) {}
