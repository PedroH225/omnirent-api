package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.item.context.ItemReassignedAuditSnapshot;

public record ItemAddressChangedEvent(
		AuditAction action,
		String actorId,
		String entityId,
		ItemReassignedAuditSnapshot currentBody,
		ItemReassignedAuditSnapshot previousBody,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent<ItemReassignedAuditSnapshot> {}
