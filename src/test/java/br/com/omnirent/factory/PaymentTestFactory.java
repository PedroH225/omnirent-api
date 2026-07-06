package br.com.omnirent.factory;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.payment.context.PaymentCanceledContext;
import br.com.omnirent.payment.context.PaymentConfirmedContext;
import br.com.omnirent.payment.context.PaymentExpiredContext;
import br.com.omnirent.payment.context.PaymentRefundContext;
import br.com.omnirent.payment.model.Payment;

public class PaymentTestFactory {


	private PaymentTestFactory() {}

    public static Payment createPayment(String rentalId, BigDecimal amount, String currency) {
        return Payment.create(rentalId, amount, currency);
    }
    
    public static PaymentCanceledContext createCanceledContext(
            String paymentId, PaymentStatus paymentStatus, String paymentIntent) {
        return new PaymentCanceledContext(paymentId, paymentStatus, paymentIntent);
    }

    public static PaymentConfirmedContext createConfirmedContext(
            String id, PaymentStatus status, String rentalId, RentalStatus rentalStatus,
            String intentId, Instant paidAt) {
        return new PaymentConfirmedContext(id, status, rentalId, rentalStatus, intentId, paidAt);
    }

    public static PaymentExpiredContext createExpiredContext(
            String paymentId, String sessionId, PaymentStatus status, String rentalId) {
        return new PaymentExpiredContext(paymentId, sessionId, status, rentalId);
    }
    
    public static PaymentRefundContext createRefundContext(PaymentStatus status) {
        return new PaymentRefundContext(status);
    }
}
