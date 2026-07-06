package br.com.omnirent.user.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.user.context.UserStatusChangeAuditSnapshot;

public record UserStatusChangeEvent(
		AuditAction action,
        String actorId,
        String userId,
        UserStatusChangeAuditSnapshot currentBody,
        UserStatusChangeAuditSnapshot previousBody,
        Instant occurredAt
	) implements DomainEvent, AuditableEvent<UserStatusChangeAuditSnapshot>,
	IntegrationEvent {

	    @Override
	    public String entityId() {
	        return userId;
	    }
}
