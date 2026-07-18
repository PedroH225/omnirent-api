package br.com.omnirent.exception.domain.apptype;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum RateLimitErrorType implements AppErrorType {

    TOO_MANY_REQUESTS("TOO_MANY_REQUESTS", "RATE_LIMIT_EXCEEDED", "too.many.requests", HttpStatus.TOO_MANY_REQUESTS);
    	
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	RateLimitErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.rate." + this.messageKey;
	}
}
