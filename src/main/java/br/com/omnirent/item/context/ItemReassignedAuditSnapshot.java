package br.com.omnirent.item.context;

import br.com.omnirent.common.audit.AuditBody;

public record ItemReassignedAuditSnapshot(
		String entityId
		) implements AuditBody {}
