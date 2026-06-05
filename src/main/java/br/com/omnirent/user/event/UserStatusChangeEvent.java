package br.com.omnirent.user.event;

import java.time.Instant;
import java.util.Map;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.DomainEvent;

public record UserStatusChangeEvent(
        String actorId,
        String userId,
        UserStatus newStatus,
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

		@Override
		public Object newData() {
			return Map.of("newStatus", newStatus);
		}
}
