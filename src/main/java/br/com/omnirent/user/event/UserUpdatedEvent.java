package br.com.omnirent.user.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.user.context.UserAuditSnapshot;

public record UserUpdatedEvent(
		AuditAction action,
        String actorId,
        String userId,
        UserAuditSnapshot currentBody,
        Instant occurredAt
	) implements DomainEvent, AuditableEvent<UserAuditSnapshot> {
	
	    @Override
	    public String entityId() {
	        return userId;
	    }

		@Override
		public UserAuditSnapshot previousBody() {
			return null;
		}
}
