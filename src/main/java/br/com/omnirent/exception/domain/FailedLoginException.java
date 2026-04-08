package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.UnauthorizedException;

public class FailedLoginException extends UnauthorizedException {
	private static final long serialVersionUID = 1L;

	public FailedLoginException() {
		super("Invalid email or password.");
	}

}
