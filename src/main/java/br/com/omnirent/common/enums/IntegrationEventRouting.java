package br.com.omnirent.common.enums;

import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.security.event.UserRegisteredEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IntegrationEventRouting {
	
	USER_REGISTERED(UserRegisteredEvent.class, "user.registered");
	
	private Class<? extends IntegrationEvent> eventClass;
	
	private String key;
	
	public static IntegrationEventRouting from(IntegrationEvent event) {
	    for (IntegrationEventRouting routing : values()) {
	        if (routing.eventClass.equals(event.getClass())) {
	            return routing;
	        }
	    }

	    throw new IllegalArgumentException(
	            "No routing configured for event: " + event.getClass().getSimpleName()
	    );
	}
}
