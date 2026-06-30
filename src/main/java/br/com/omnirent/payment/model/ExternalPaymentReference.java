package br.com.omnirent.payment.model;

import br.com.omnirent.payment.enums.PaymentProvider;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExternalPaymentReference {
	
	@Enumerated(EnumType.STRING)
	private PaymentProvider paymentProvider;
	
	private String externalPaymentId;
	
	private String paymentIntent;

	protected ExternalPaymentReference(
			PaymentProvider provider, String externalPaymentId, String paymentIntent) {
		this.paymentProvider = provider;
		this.externalPaymentId = externalPaymentId;
		this.paymentIntent = paymentIntent;
	}
}
