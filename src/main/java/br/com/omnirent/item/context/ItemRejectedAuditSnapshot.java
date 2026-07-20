package br.com.omnirent.item.context;

import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.ItemRejectionReason;
import br.com.omnirent.common.enums.ItemStatus;

public record ItemRejectedAuditSnapshot(
		ItemStatus status,
		ItemRejectionReason reason
		) implements AuditBody {}
