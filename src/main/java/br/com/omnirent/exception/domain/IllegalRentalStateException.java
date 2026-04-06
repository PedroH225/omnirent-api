package br.com.omnirent.exception.domain;

import org.springframework.http.HttpStatus;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.common.BusinessException;

public class IllegalRentalStateException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public IllegalRentalStateException(RentalStatus currentStatus, RentalStatus targetStatus) {
		super(String.format("Illegal rental state transition: %s to %s", currentStatus, targetStatus),
				HttpStatus.CONFLICT, "Illegal rental state transition");
	}

}
