package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.event.DomainEvent;

public record ItemAddressChangedEvent(
		String actorId,
		String entityId,
		String newAddressID,
		Instant occurredAt
) implements DomainEvent {}
