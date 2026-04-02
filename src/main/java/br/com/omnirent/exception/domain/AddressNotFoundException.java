package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.NotFoundException;

public class AddressNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public AddressNotFoundException() {
		super("Address not found.");
		
	}

}
