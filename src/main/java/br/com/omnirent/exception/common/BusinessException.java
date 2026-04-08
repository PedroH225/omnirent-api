package br.com.omnirent.exception.common;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final HttpStatus httpStatus;
	
	private final String error;

	protected BusinessException(String message, HttpStatus httpStatus, String error) {
		super(message);
		this.httpStatus = httpStatus;
		this.error = error;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getError() {
		return error;
	}

}
