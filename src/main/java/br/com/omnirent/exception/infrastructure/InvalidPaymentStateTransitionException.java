package br.com.omnirent.exception.infrastructure;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.exception.common.DomainException;

public class InvalidPaymentStateTransitionException extends DomainException {
	private static final long serialVersionUID = 1L;

	public InvalidPaymentStateTransitionException(
			PaymentStatus currentStatus, PaymentStatus targetStatus) {
		super(String.format(
				"Invalid payment state transition: %s -> %s",
				currentStatus, targetStatus));
	}
}
