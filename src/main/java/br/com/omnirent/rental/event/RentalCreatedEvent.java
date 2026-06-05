package br.com.omnirent.rental.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.rental.context.RentalAuditSnapshot;

public record RentalCreatedEvent(
		String actorId,
		String entityId,
		RentalAuditSnapshot data,
		Instant occurredAt
) implements DomainEvent, AuditableEvent {

	@Override
	public Object oldData() {
		return null;
	}

	@Override
	public Object newData() {
		return data;
	}
}
