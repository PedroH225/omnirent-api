package br.com.omnirent.common.event;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import br.com.omnirent.address.event.AddressUpdatedEvent;
import br.com.omnirent.common.enums.DomainEventType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingEventListener {
		
	private void logDefaultEvent(DomainEventType event, String actorId, String entityId, Instant ocurredAt) {
    	log.info("""
        		
	        	event= {}
	        	actorId= {}
	        	targetEntityId= {}
	        	occurredAt= {}
	        """,
	        event, actorId, entityId, ocurredAt);
	}

    @EventListener
    public void handle(AddressUpdatedEvent event) {
    	logDefaultEvent(event.eventType(), event.actorId(), event.addressId(), event.occurredAt());
    }

}
