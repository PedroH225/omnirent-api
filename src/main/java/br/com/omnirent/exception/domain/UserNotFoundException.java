package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.NotFoundException;

public class UserNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public UserNotFoundException() {
		super("User not found.");
		
	}

}
