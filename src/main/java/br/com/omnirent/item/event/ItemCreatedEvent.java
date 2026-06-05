package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.item.context.ItemAuditSnapshot;

public record ItemCreatedEvent(
		String actorId,
		String entityId,
		ItemAuditSnapshot data,
		Instant occurredAt
) implements DomainEvent, AuditableEvent {

	@Override
	public Object oldData() {
		return null;
	}

	@Override
	public Object newData() {
		return data;
	}
}
