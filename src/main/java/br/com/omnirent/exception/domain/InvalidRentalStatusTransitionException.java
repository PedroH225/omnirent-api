package br.com.omnirent.exception.domain;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.common.DomainException;

public class InvalidRentalStatusTransitionException extends DomainException {
	private static final long serialVersionUID = 1L;

	public InvalidRentalStatusTransitionException(
			RentalStatus currentStatus, RentalStatus targetStatus) {
		super(String.format("Invalid rental status transition: %s -> %s",
				currentStatus, targetStatus));
	}

}
