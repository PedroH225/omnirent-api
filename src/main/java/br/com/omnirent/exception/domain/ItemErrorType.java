package br.com.omnirent.exception.domain;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum ItemErrorType implements AppErrorType {

    NOT_FOUND("NOT_FOUND", "ITEM_NOT_FOUND", "not_found", HttpStatus.NOT_FOUND),
    BLOCKED("BLOCKED", "ITEM_BLOCKED", "blocked", HttpStatus.FORBIDDEN);
    
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	ItemErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.item." + this.messageKey;
	}
}
