package br.com.omnirent.exception.common;

import org.springframework.http.HttpStatus;

public abstract class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private final String DEFAULT_ERROR_PREFIX = "error.";
	
	private final HttpStatus httpStatus;
	
	private final String error;
	
	private String key;
	

	protected BusinessException(String message, HttpStatus httpStatus, String error, String key) {
		super(message);
		this.httpStatus = httpStatus;
		this.error = error;
		this.key = key;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String getError() {
		return error;
	}

	public String getErrorKey() {
		return DEFAULT_ERROR_PREFIX + key;
	}
	
	public String getMessageKey() {
		return DEFAULT_ERROR_PREFIX + key + ".message";
	}
}
