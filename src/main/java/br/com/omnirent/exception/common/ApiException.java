package br.com.omnirent.exception.common;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
	private static final long serialVersionUID = 1L;
		
	private final AppErrorType errorType;
	
    private final Object[] args;

	public ApiException(AppErrorType errorType, Object... args) {
		this.errorType = errorType;
		this.args = args;
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
