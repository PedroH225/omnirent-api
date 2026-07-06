package br.com.omnirent.security.event;

import java.time.Instant;
import java.util.Locale;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.user.context.UserAuditSnapshot;

public record UserRegisteredEvent(
		AuditAction action,
        String entityId,
        UserAuditSnapshot currentBody,
        Instant occurredAt,
        Locale locale
	) implements DomainEvent, AuditableEvent<UserAuditSnapshot>, IntegrationEvent {

	@Override
	public UserAuditSnapshot previousBody() {
		return null;
	}

	@Override
	public String actorId() {
		return null;
	}
}