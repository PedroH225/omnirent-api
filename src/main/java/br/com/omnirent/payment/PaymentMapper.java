package br.com.omnirent.payment;

import java.time.Instant;

import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.payment.context.PaymentConfirmedContext;
import br.com.omnirent.payment.context.audit.PaymentConfirmedAuditSnapshot;
import br.com.omnirent.payment.context.audit.PaymentCreatedAuditSnapshot;
import br.com.omnirent.payment.model.ExternalPaymentReference;
import br.com.omnirent.payment.model.Payment;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PaymentMapper {

	public PaymentCreatedAuditSnapshot toCreatedSnapshot(Payment payment) {
		ExternalPaymentReference externalReference = payment.getExternalReference();
		return new PaymentCreatedAuditSnapshot(
				payment.getRentalId(), payment.getStatus(),
				externalReference.getPaymentProvider(), payment.getAmount(),
				payment.getCurrency(), externalReference.getExternalPaymentId());
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
}
