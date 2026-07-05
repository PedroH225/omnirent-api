package br.com.omnirent.payment.context;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.PaymentStatus;

public record ReopenPaymentContext(
		String paymentId,
		String rentalId,
		BigDecimal finalPrice,
		PaymentStatus currentPaymentStatus){

}
