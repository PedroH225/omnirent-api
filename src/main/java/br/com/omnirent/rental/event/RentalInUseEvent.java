package br.com.omnirent.rental.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.rental.context.RentalInUseAuditSnapshot;

public record RentalInUseEvent(
		AuditAction action,
		String actorId,
		String entityId,
		RentalInUseAuditSnapshot currentBody,
		RentalInUseAuditSnapshot previousBody,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent<RentalInUseAuditSnapshot>,
	IntegrationEvent {}
