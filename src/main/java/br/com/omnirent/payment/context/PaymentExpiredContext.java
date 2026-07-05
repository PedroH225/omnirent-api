package br.com.omnirent.payment.context;

import br.com.omnirent.common.enums.PaymentStatus;

public record PaymentExpiredContext(
		String paymentId,
		String sessionId,
		PaymentStatus status,
		String rentalId
		) {}
