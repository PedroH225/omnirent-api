package br.com.omnirent.payment;

import java.time.Instant;

import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.payment.context.PaymentConfirmedContext;
import br.com.omnirent.payment.context.ReopenPaymentContext;
import br.com.omnirent.payment.context.audit.PaymentAuditSnapshot;
import br.com.omnirent.payment.context.audit.PaymentConfirmedAuditSnapshot;
import br.com.omnirent.payment.dto.PaymentCreatedResponse;
import br.com.omnirent.payment.enums.PaymentProvider;
import br.com.omnirent.payment.model.ExternalPaymentReference;
import br.com.omnirent.payment.model.Payment;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PaymentMapper {

	public PaymentAuditSnapshot toAuditSnapshot(Payment payment) {
		ExternalPaymentReference externalReference = payment.getExternalReference();
		return new PaymentAuditSnapshot(
				payment.getRentalId(), payment.getStatus(),
				externalReference.getPaymentProvider(), payment.getAmount(),
				payment.getCurrency(), externalReference.getExternalPaymentId(), null);
	}
	
	public PaymentAuditSnapshot toAuditSnapshot(ReopenPaymentContext payment) {
		return new PaymentAuditSnapshot(
				payment.rentalId(), payment.currentPaymentStatus(),
				payment.provider(), payment.finalPrice(), payment.currency(), 
				payment.sessionId(), payment.paymentIntent());
	}
	
	public PaymentAuditSnapshot toAuditSnapshot(ReopenPaymentContext context, PaymentStatus targetStatus, String currency,
			PaymentProvider stripe, String sessionId, String paymentIntent) {
		return new PaymentAuditSnapshot(
				context.rentalId(), targetStatus, stripe, context.finalPrice(),
				currency, sessionId, paymentIntent);
	}
	
	public PaymentConfirmedAuditSnapshot toConfirmedSnapshot(String rentalId, PaymentStatus status,
			String paymentIntent, Instant paidAt) {
		return new PaymentConfirmedAuditSnapshot(rentalId, status, paymentIntent, paidAt);
	}
	
	public PaymentConfirmedAuditSnapshot toConfirmedSnapshot(PaymentConfirmedContext context) {
		return new PaymentConfirmedAuditSnapshot(
				context.rentalId(), context.status(), 
				context.paymentIntentId(), context.paidAt());
	}
	
	public PaymentCreatedResponse toPaymentResponse(PaymentAuditSnapshot snapshot) {
		return new PaymentCreatedResponse(snapshot.status(), snapshot.provider(),
				snapshot.amount(), snapshot.currency());
	}
}
