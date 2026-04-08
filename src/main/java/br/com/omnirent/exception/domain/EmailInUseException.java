package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.ConflictException;

public class EmailInUseException extends ConflictException {
	private static final long serialVersionUID = 1L;

	public EmailInUseException(String email) {
		super("Email already in use: " + email);
	}

}
