package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.common.enums.UserStatus;

public record ItemPermissionData(
		ItemStatus itemStatus,
		String ownerId,
		UserStatus ownerStatus) {}
