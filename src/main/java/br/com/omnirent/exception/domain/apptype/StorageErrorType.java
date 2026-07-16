package br.com.omnirent.exception.domain.apptype;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum StorageErrorType implements AppErrorType {

	STORAGE_UNAVAILABLE("STORAGE_UNAVAILABLE", "STORAGE_UNAVAILABLE", "unavailable", HttpStatus.SERVICE_UNAVAILABLE),
	STORAGE_UPLOAD_FAILED("STORAGE_UPLOAD_FAILED", "STORAGE_UPLOAD_FAILED", "upload.failed", HttpStatus.INTERNAL_SERVER_ERROR),
	STORAGE_ACCESS_DENIED("STORAGE_ACCESS_DENIED", "STORAGE_ACCESS_DENIED", "access.denied", HttpStatus.FORBIDDEN),
	STORAGE_RATE_LIMITED("STORAGE_RATE_LIMITED", "STORAGE_RATE_LIMITED", "rate.limited", HttpStatus.TOO_MANY_REQUESTS);
	
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	StorageErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.storage." + this.messageKey;
	}
}
