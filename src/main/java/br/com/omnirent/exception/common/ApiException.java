package br.com.omnirent.exception.common;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;
		
	private final AppErrorType errorType;

	public ApiException(AppErrorType errorType) {
		this.errorType = errorType;
	}
	
	public String getErrorType() {
		return errorType.getErrorType();
	}
	
	public String getErrorCode() {
		return errorType.getErrorCode();
	}
	
	public String getMessageKey() {
		return errorType.getMessageKey();
	}
	
	public HttpStatus getHttpStatus() {
		return errorType.getHttpCode();
	}
}
