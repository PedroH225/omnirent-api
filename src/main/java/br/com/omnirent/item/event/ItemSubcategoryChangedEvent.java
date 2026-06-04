package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.event.DomainEvent;

public record ItemSubcategoryChangedEvent(
		String actorId,
		String entityId,
		String newSubcategoryID,
		Instant occurredAt
) implements DomainEvent {}
