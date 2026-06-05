package br.com.omnirent.address.event;

import java.time.Instant;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.common.event.DomainEvent;

public record AddressDeletedEvent(		
		String actorId,
		String addressId,
		AddressAuditSnapshot data,
		Instant occurredAt
		) implements DomainEvent {
		
		@Override
		public String entityId() {
			return addressId;
		}
} 