package br.com.omnirent.address.event;

import java.time.Instant;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;

public record AddressDeletedEvent(		
		String actorId,
		String addressId,
		AddressAuditSnapshot data,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent {
		
		@Override
		public String entityId() {
			return addressId;
		}

		@Override
		public Object oldData() {
			return data;
		}

		@Override
		public Object newData() {
			return null;
		}
} 