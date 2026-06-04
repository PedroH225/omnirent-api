package br.com.omnirent.user.event;

import java.time.Instant;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.DomainEvent;

public record UserStatusChangeEvent(
        String actorId,
        String userId,
        UserStatus newStatus,
        Instant occurredAt
	) implements DomainEvent {

	    @Override
	    public String entityId() {
	        return userId;
	    }
}
