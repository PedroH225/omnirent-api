package br.com.omnirent.rental.event;

import java.time.Instant;
import java.util.Map;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.infrastructure.IntegrationEvent;

public record RentalInUseEvent(
		String actorId,
		String entityId,
		RentalStatus oldStatus,
		Instant startDate,
		Instant endDate,
		Instant occurredAt
		) implements AuditableEvent, IntegrationEvent {

	@Override
	public Object oldData() {
		return Map.of("oldStatus", oldStatus);
	}

	@Override
	public Object newData() {
		return Map.of(
				"startDate", startDate,
				"endDate", endDate
				);
	}
}
