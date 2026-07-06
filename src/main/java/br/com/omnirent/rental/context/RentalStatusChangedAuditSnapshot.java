package br.com.omnirent.rental.context;

import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.RentalStatus;

public record RentalStatusChangedAuditSnapshot(
		RentalStatus status
		) implements AuditBody {}
