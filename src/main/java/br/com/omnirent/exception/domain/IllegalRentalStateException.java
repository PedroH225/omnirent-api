package br.com.omnirent.exception.domain;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.common.ConflictException;

public class IllegalRentalStateException extends ConflictException {
	private static final long serialVersionUID = 1L;

	public IllegalRentalStateException(RentalStatus currentStatus, RentalStatus targetStatus) {
		super(String.format("Illegal rental state transition: %s to %s",
				currentStatus, targetStatus));
	}

}
