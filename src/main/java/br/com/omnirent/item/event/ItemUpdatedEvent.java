package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.item.context.ItemAuditSnapshot;

public record ItemUpdatedEvent(
		String actorId,
		String entityId,
		ItemAuditSnapshot data,
		Instant occurredAt
) implements DomainEvent {}
