package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemStatus;

public record SearchItemFilter(
		String name,
		ItemStatus itemStatus
	) {}
