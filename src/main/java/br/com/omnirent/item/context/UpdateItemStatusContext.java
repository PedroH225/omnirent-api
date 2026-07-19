package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.common.enums.UserStatus;

public record UpdateItemStatusContext(
		String id,
		ItemStatus currentStatus,
		String ownerId,
		UserStatus ownerStatus
		) {}
