package br.com.omnirent.security.event;

import java.time.Instant;

import br.com.omnirent.common.enums.SecurityEventType;
import br.com.omnirent.common.event.SecurityEvent;

public record UserLoggedInEvent(
        String userId,
        String ip,
        String userAgent,
        boolean success,
        Instant occurredAt
		) implements SecurityEvent {
		@Override
		public SecurityEventType eventType() {
			return SecurityEventType.USER_LOGGED_IN;
		}
}