package br.com.omnirent.common.event;

import java.time.Instant;

import br.com.omnirent.common.enums.DomainEventType;

public interface DomainEvent {
	
	DomainEventType eventType();
	
	Instant occurredAt();
	
}
