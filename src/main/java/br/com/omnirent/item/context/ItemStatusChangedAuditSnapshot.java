package br.com.omnirent.item.context;

import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.ItemStatus;

public record ItemStatusChangedAuditSnapshot(
		ItemStatus status
		) implements AuditBody {}
