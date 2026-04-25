package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemStatus;

public record ChangeItemAddressContext(
		String id,
		String ownerId,
		String currentAddressId,
		ItemStatus status) {}
