package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.DomainException;

public class PaymentNotFoundException extends DomainException {
	private static final long serialVersionUID = 1L;

	public PaymentNotFoundException(String paymentId) {
		super("Payment not found: " + paymentId);
	}

}
