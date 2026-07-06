package br.com.omnirent.payment.context.audit;

import java.time.Instant;

import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.PaymentStatus;

public record PaymentConfirmedAuditSnapshot(
	    String rentalId,
	    PaymentStatus status,
	    String paymentIntent,
	    Instant paidAt
	) implements AuditBody {}
