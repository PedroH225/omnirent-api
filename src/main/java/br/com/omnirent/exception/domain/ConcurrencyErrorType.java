package br.com.omnirent.exception.domain;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum ConcurrencyErrorType implements AppErrorType {

    OPTMISTIC_LOCK("OPTIMISTIC_LOCK", "OPTIMISTIC_LOCK", "optimistic_lock", HttpStatus.CONFLICT);
    	
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	ConcurrencyErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.concurrency." + this.messageKey;
	}
}
