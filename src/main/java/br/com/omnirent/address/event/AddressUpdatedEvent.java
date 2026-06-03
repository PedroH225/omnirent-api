package br.com.omnirent.address.event;

import java.time.Instant;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.common.enums.DomainEventType;
import br.com.omnirent.common.event.DomainEvent;

public record AddressUpdatedEvent(
		String actorId,
		String addressId,
		DomainEventType eventType,
		AddressAuditSnapshot oldData,
		AddressAuditSnapshot newData,
		Instant occurredAt
		) implements DomainEvent {} 
