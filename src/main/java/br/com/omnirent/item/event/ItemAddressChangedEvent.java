package br.com.omnirent.item.event;

import java.time.Instant;
import java.util.Map;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;

public record ItemAddressChangedEvent(
		String actorId,
		String entityId,
		String newAddressID,
		Instant occurredAt
) implements DomainEvent, AuditableEvent {

	@Override
	public Object oldData() {
		return null;
	}

	@Override
	public Object newData() {
		return Map.of("newAddressId", newAddressID);
	}
}
