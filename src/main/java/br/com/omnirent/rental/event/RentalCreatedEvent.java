package br.com.omnirent.rental.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.rental.context.RentalAuditSnapshot;

public record RentalCreatedEvent(
		String actorId,
		String entityId,
		RentalAuditSnapshot data
	) implements AuditableEvent, IntegrationEvent {
	
		@Override
		public Object oldData() {
			return null;
		}
	
		@Override
		public Instant occurredAt() {
			return Instant.now();
		}
		
		@Override
		public Object newData() {
			return data;
		}
	}
