package br.com.omnirent.address.event;

import java.time.Instant;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.common.event.DomainEvent;

public record AddressUpdatedEvent(
		String actorId,
		String addressId,
		AddressAuditSnapshot oldData,
		AddressAuditSnapshot newData,
		Instant occurredAt
		) implements DomainEvent {
		
		@Override
		public String entityId() {
			return addressId;
		}
} 
