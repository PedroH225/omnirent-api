package br.com.omnirent.security.event;

import java.time.Instant;

import br.com.omnirent.common.enums.DomainEventType;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.user.context.UserAuditSnapshot;

public record UserRegisteredEvent(
        String userId,
        UserAuditSnapshot newUser,
        Instant occurredAt
	) implements DomainEvent {
	
	    @Override
	    public DomainEventType eventType() {
	        return DomainEventType.USER_REGISTERED;
	    }
	
	    @Override
	    public String entityId() {
	        return userId;
	    }

		@Override
		public String actorId() {
			return userId;
		}
}