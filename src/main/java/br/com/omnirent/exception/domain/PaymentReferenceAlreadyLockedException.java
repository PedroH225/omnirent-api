package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.DomainException;

public class PaymentReferenceAlreadyLockedException extends DomainException {
	private static final long serialVersionUID = 1L;

	public PaymentReferenceAlreadyLockedException() {
		super("Cannot attach reference after payment processing started");
	}

}
