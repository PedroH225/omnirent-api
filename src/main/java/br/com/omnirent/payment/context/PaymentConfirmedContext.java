package br.com.omnirent.payment.context;

import java.time.Instant;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalStatus;

public record PaymentConfirmedContext(
		String id,
		PaymentStatus status,
		String rentalId,
		RentalStatus rentalStatus,
		String paymentIntentId,
		Instant paidAt
	) {}
