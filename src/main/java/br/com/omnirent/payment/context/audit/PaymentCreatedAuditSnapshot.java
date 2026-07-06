package br.com.omnirent.payment.context.audit;

import java.math.BigDecimal;

import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.payment.enums.PaymentProvider;

public record PaymentCreatedAuditSnapshot(
	    String paymentId,
	    String rentalId,
	    PaymentStatus status,
	    PaymentProvider provider,
	    BigDecimal amount,
	    String currency,
	    String externalPaymentId
	) implements AuditBody {}
