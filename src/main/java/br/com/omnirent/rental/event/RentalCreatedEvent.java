package br.com.omnirent.rental.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.rental.context.RentalAuditSnapshot;

public record RentalCreatedEvent(
		AuditAction action,
		String actorId,
		String entityId,
		RentalAuditSnapshot currentBody,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent<RentalAuditSnapshot>,
	IntegrationEvent {

	@Override
	public RentalAuditSnapshot previousBody() {
		return null;
	}
}
