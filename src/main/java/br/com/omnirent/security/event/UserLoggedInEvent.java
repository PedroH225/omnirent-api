package br.com.omnirent.security.event;

import java.time.Instant;

import br.com.omnirent.common.event.SecurityEvent;
import br.com.omnirent.security.auth.provider.AuthProvider;

public record UserLoggedInEvent(
        String userId,
        String ip,
        String userAgent,
        AuthProvider provider,
        boolean success,
        Instant occurredAt
		) implements SecurityEvent {
}