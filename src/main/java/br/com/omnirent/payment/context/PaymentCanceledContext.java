package br.com.omnirent.payment.context;

import br.com.omnirent.common.enums.PaymentStatus;

public record PaymentCanceledContext(
		String paymentId,
		PaymentStatus paymentStatus,
		String paymentIntent) {}
