package br.com.omnirent.rental.event;

import java.time.Instant;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.DomainEvent;

public record RentalStatusChangedEvent(
		String actorId,
		String entityId,
		RentalStatus oldStatus,
		RentalStatus newStatus,	
		Instant occurredAt
		) implements DomainEvent {}
