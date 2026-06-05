package br.com.omnirent.user.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.user.context.UserAuditSnapshot;

public record UserUpdatedEvent(
        String actorId,
        String userId,
        UserAuditSnapshot newData,
        Instant occurredAt
	) implements DomainEvent, AuditableEvent {
	
	    @Override
	    public String entityId() {
	        return userId;
	    }

		@Override
		public Object oldData() {
			return null;
		}
}
