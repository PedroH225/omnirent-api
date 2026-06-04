package br.com.omnirent.common.event;

import java.time.Instant;

import br.com.omnirent.common.enums.SecurityEventType;

public interface SecurityEvent {
	
	SecurityEventType eventType();
	
    String userId();
    
    String ip();
    
    String userAgent();
    
    boolean success();
    
    Instant occurredAt();
}
