package br.com.omnirent.rental;

import java.time.Instant;

import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.rental.context.RentalAuditSnapshot;

public record RentalCreatedEvent(
		String actorId,
		String entityId,
		RentalAuditSnapshot data,
		Instant occurredAt
) implements DomainEvent {}
