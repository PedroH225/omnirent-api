package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemRejectionReason;

public record ItemRejectedRequestDto(
		ItemRejectionReason reason
		) {}
