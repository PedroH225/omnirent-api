package br.com.omnirent.rental.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.DomainEvent;

public record RentalInUseEvent(
		String actorId,
		String entityId,
		RentalStatus oldStatus,
		LocalDateTime startDate,
		LocalDateTime endDate,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent {

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
