package br.com.omnirent.exception.common;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends BusinessException{
	private static final long serialVersionUID = 1L;

	public UnauthorizedException(String message) {
		super(message, HttpStatus.UNAUTHORIZED, "Unauthorized");
	}
}
