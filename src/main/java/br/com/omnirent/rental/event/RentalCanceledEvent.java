package br.com.omnirent.rental.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.rental.context.RentalStatusChangedAuditSnapshot;

public record RentalCanceledEvent(
		AuditAction action,
		String actorId,
		String entityId,
		RentalStatusChangedAuditSnapshot currentBody,
		RentalStatusChangedAuditSnapshot previousBody,	
		Instant occurredAt
		) implements DomainEvent, AuditableEvent<RentalStatusChangedAuditSnapshot>,
	IntegrationEvent {}
