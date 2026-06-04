package br.com.omnirent.common.event;

import java.time.Instant;

public interface DomainEvent {
	
	default String eventType() {
	    return getClass()
	            .getSimpleName()
	            .replace("Event", "")
	            .replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .toUpperCase();    
	}
	
	String actorId();
	
	String entityId();
	
	Instant occurredAt();
	
}
