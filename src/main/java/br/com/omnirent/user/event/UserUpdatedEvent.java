package br.com.omnirent.user.event;

import java.time.Instant;

import br.com.omnirent.common.enums.DomainEventType;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.user.context.UserAuditSnapshot;

public record UserUpdatedEvent(
        String actorId,
        String userId,
        UserAuditSnapshot newData,
        Instant occurredAt
	) implements DomainEvent {
	
	    @Override
	    public DomainEventType eventType() {
	        return DomainEventType.USER_UPDATED;
	    }
	
	    @Override
	    public String entityId() {
	        return userId;
	    }
}
