package br.com.omnirent.exception.domain;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum CommonErrorType implements AppErrorType {

	FORBIDDEN("FORBIDDEN", "FORBIDDEN", "forbidden", HttpStatus.FORBIDDEN),
    ILLEGAL_ENUMERATION("CONFLICT", "ILLEGAL_ENUMERATION", "illegal_enumeration", HttpStatus.BAD_REQUEST),
	VALIDATION_ERROR("CONFLICT", "VALIDATION_ERROR", "validation_error", HttpStatus.CONFLICT),
    INTERNAL_ERROR("INTERNAL_SERVER_ERROR", "INTERNAL_SERVER_ERROR", "internal_server_error", HttpStatus.INTERNAL_SERVER_ERROR);
	
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	CommonErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.common." + this.messageKey;
	}
}
