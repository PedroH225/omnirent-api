package br.com.omnirent.exception.domain.apptype;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum PaymentErrorType implements AppErrorType {

    NOT_FOUND("NOT_FOUND", "PAYMENT_NOT_FOUND", "not.found", HttpStatus.NOT_FOUND);
    	
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	PaymentErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.payment." + this.messageKey;
	}
}
