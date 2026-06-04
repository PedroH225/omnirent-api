package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.common.event.DomainEvent;

public record ItemStatusUpdatedEvent(
		String actorId,
		String entityId,
		ItemStatus newStatus,
		Instant occurredAt
) implements DomainEvent {}
