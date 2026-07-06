package br.com.omnirent.payment.event;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditableEvent;
import br.com.omnirent.common.event.DomainEvent;
import br.com.omnirent.payment.context.audit.PaymentAuditSnapshot;

public record PaymentReinitializedEvent(
		AuditAction action,
		String actorId,
		String entityId,
		PaymentAuditSnapshot currentBody,
		PaymentAuditSnapshot previousBody,
		Instant occurredAt
		) implements DomainEvent, AuditableEvent<PaymentAuditSnapshot> {}
