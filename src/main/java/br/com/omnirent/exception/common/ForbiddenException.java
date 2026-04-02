package br.com.omnirent.exception.common;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public ForbiddenException(String message) {
		super(message, HttpStatus.FORBIDDEN, "Forbidden");
	}

}
