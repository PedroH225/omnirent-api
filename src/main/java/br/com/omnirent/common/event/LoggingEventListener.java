package br.com.omnirent.common.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoggingEventListener {
    @EventListener
    public void handle(DomainEvent event) {
    	log.info(
    		"""
        		
	        	event= {}
	        	actorId= {}
	        	targetEntityId= {}
	        	occurredAt= {}
	        """,
	        event.eventType(), event.actorId(), event.entityId(), event.occurredAt());
    	}
    
    @EventListener
    public void handle(SecurityEvent event) {
    	log.info(
    			"""
    			
		    		security_event= {}
		    		userId= {}
		    		ip= {}
		    		userAgent= {}
		    		success= {}
		    		occurredAt= {}
    			""",
                event.eventType(), event.userId(), event.ip(), event.userAgent(),
                event.success(), event.occurredAt());
    }
}
