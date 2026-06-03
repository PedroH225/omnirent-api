package br.com.omnirent.user.event;

import java.time.Instant;

import br.com.omnirent.common.enums.DomainEventType;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.DomainEvent;

public record UserStatusChangeEvent(
        String actorId,
        String userId,
        UserStatus newStatus,
        Instant occurredAt
	) implements DomainEvent {
	
	    @Override
	    public DomainEventType eventType() {
	        return DomainEventType.USER_STATUS_UPDATED;
	    }
	
	    @Override
	    public String entityId() {
	        return userId;
	    }
}
