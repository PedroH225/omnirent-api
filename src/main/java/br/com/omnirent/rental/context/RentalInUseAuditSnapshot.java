package br.com.omnirent.rental.context;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.RentalStatus;

public record RentalInUseAuditSnapshot(
		RentalStatus oldStatus,
		Instant startDate,
		Instant endDate
		) implements AuditBody {}
