package br.com.omnirent.payment.dto;

import br.com.omnirent.common.enums.PaymentStatus;

public record CheckoutCompletedDTO(
		String rentalId,
		String checkoutUrl,
		PaymentStatus status
		) {}
