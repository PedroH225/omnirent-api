package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.item.context.ItemAuditSnapshot;

public record ItemUpdatedEvent(
		AuditAction action,
		String actorId,
		String entityId,
		ItemAuditSnapshot currentBody,
		ItemAuditSnapshot previousBody,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent<ItemAuditSnapshot> {}
