package br.com.omnirent.rental.event;

import java.time.Instant;
import java.util.Map;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.infrastructure.IntegrationEvent;

public record RentalStatusChangedEvent(
		String actorId,
		String entityId,
		RentalStatus oldStatus,
		RentalStatus newStatus,	
		Instant occurredAt
		) implements AuditableEvent, IntegrationEvent {

		@Override
		public Object oldData() {
			return Map.of("oldStatus", oldStatus);
		}
	
		@Override
		public Object newData() {
			return Map.of("newStatus", newStatus);
		}
	}
