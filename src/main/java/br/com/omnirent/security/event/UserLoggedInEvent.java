package br.com.omnirent.security.event;

import java.time.Instant;

import br.com.omnirent.common.event.SecurityEvent;

public record UserLoggedInEvent(
        String userId,
        String ip,
        String userAgent,
        boolean success,
        Instant occurredAt
		) implements SecurityEvent {
}