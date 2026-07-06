package br.com.omnirent.payment.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.payment.context.audit.PaymentCreatedAuditSnapshot;

public record PaymentCreatedEvent(
		AuditAction action,
		String actorId,
		String entityId,
		PaymentCreatedAuditSnapshot currentBody,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent<PaymentCreatedAuditSnapshot> {

		@Override
		public PaymentCreatedAuditSnapshot previousBody() {
			return null;
		}
	}
