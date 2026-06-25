package br.com.omnirent.user.event;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.infrastructure.IntegrationEvent;

public record UserStatusChangeEvent(
        String actorId,
        String userId,
        UserStatus newStatus,
        String email,
        String username,
        Locale locale
	) implements AuditableEvent, IntegrationEvent {

	    @Override
	    public String entityId() {
	        return userId;
	    }

		@Override
		public Object oldData() {
			return null;
		}
		
		public Instant occurredAt() {
			return Instant.now();
		}

		@Override
		public Object newData() {
			return Map.of("newStatus", newStatus);
		}
}
