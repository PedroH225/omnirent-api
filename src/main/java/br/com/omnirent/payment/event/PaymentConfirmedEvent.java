package br.com.omnirent.payment.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.payment.context.audit.PaymentConfirmedAuditSnapshot;

public record PaymentConfirmedEvent(
		AuditAction action,
		String actorId,
		String entityId,
		PaymentConfirmedAuditSnapshot currentBody,
		PaymentConfirmedAuditSnapshot previousBody,
		Instant occurredAt
	) implements DomainEvent, AuditableEvent<PaymentConfirmedAuditSnapshot> {}
