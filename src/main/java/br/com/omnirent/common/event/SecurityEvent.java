package br.com.omnirent.common.event;

import java.time.Instant;

import br.com.omnirent.security.auth.provider.AuthProvider;

public interface SecurityEvent {
	
	default String eventType() {
	    return getClass()
	            .getSimpleName()
	            .replace("Event", "")
	            .replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .toUpperCase();    
	}
	
    String userId();
    
    String ip();
    
    String userAgent();
    
    AuthProvider provider();
    
    boolean success();
    
    Instant occurredAt();
}
