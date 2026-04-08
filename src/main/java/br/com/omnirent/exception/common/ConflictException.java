package br.com.omnirent.exception.common;

import org.springframework.http.HttpStatus;

public class ConflictException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public ConflictException(String message) {
		super(message, HttpStatus.CONFLICT, "Conflict.");
	}

}
