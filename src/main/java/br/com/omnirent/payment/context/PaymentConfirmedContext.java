package br.com.omnirent.payment.context;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalStatus;

public record PaymentConfirmedContext(
		String id,
		PaymentStatus status,
		String rentalId,
		RentalStatus rentalStatus) {}
