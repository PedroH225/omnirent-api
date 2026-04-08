package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.NotFoundException;

public class RentalNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public RentalNotFoundException() {
		super("Rental not found.");
		
	}

}
