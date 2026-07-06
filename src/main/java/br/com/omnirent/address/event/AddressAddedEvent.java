package br.com.omnirent.address.event;

import java.time.Instant;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;

public record AddressAddedEvent(	
		AuditAction action,
		String actorId,
		String addressId,
		AddressAuditSnapshot currentBody,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent<AddressAuditSnapshot> {
		
		@Override
		public String entityId() {
			return addressId;
		}

		@Override
		public AddressAuditSnapshot previousBody() {
			return null;
		}
} 
