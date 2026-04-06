package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.ForbiddenException;

public class FailedLoginException extends ForbiddenException {
	private static final long serialVersionUID = 1L;

	public FailedLoginException() {
		super("Invalid email or password.");
	}

}
