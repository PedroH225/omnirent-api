package br.com.omnirent.item.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.item.context.ItemAuditSnapshot;

public record ItemCreatedEvent(
		String actorId,
		String entityId,
		ItemAuditSnapshot data
) implements AuditableEvent, IntegrationEvent {

	@Override
	public Object oldData() {
		return null;
	}

	@Override
	public Object newData() {
		return data;
	}
	
	@Override
	public Instant occurredAt() {
		return Instant.now();
	}
}
