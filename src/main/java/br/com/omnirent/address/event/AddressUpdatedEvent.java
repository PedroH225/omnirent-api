package br.com.omnirent.address.event;

import java.time.Instant;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;

public record AddressUpdatedEvent(
		String actorId,
		String addressId,
		AddressAuditSnapshot oldData,
		AddressAuditSnapshot newData,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent {
		
		@Override
		public String entityId() {
			return addressId;
		}
} 
