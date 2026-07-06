package br.com.omnirent.payment.context;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.payment.enums.PaymentProvider;

public record ReopenPaymentContext(
		String paymentId,
		String rentalId,
		BigDecimal finalPrice,
		String currency,
		PaymentStatus currentPaymentStatus,
		PaymentProvider provider,
		String sessionId,
		String paymentIntent){

}
