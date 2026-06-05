package br.com.omnirent.rental.event;

import java.time.Instant;
import java.time.LocalDateTime;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.DomainEvent;

public record RentalInUseEvent(
		String actorId,
		String entityId,
		RentalStatus oldStatus,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Instant occurredAt
		) implements DomainEvent {}
