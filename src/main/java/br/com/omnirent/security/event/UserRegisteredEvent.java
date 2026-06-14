package br.com.omnirent.security.event;

import java.time.Instant;

import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.user.context.UserAuditSnapshot;

public record UserRegisteredEvent(
        String userId,
        UserAuditSnapshot newUser,
        Instant occurredAt
	) implements DomainEvent, IntegrationEvent {
	
	    @Override
	    public String entityId() {
	        return userId;
	    }

		@Override
		public String actorId() {
			return userId;
		}
}