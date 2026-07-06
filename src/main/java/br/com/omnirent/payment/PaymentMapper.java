package br.com.omnirent.payment;

import org.springframework.stereotype.Component;

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
}
