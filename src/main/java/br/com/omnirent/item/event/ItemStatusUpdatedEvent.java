package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.item.context.ItemStatusChangedAuditSnapshot;

public record ItemStatusUpdatedEvent(
		AuditAction action,
		String actorId,
		String entityId,
		ItemStatusChangedAuditSnapshot currentBody,
		ItemStatusChangedAuditSnapshot previousBody,
		Instant occurredAt
		) implements IntegrationEvent, DomainEvent, AuditableEvent<ItemStatusChangedAuditSnapshot> {}
