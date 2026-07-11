package br.com.omnirent.exception.domain.apptype;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum UserErrorType implements AppErrorType {

    NOT_FOUND("NOT_FOUND", "USER_NOT_FOUND", "not_found", HttpStatus.NOT_FOUND),
    INACTIVE("FORBIDDEN", "USER_INACTIVE", "inactive", HttpStatus.FORBIDDEN),
    BANNED("FORBIDDEN", "USER_BANNED", "banned", HttpStatus.FORBIDDEN),
    EMAIL_ALREADY_IN_USE("CONFLICT", "EMAIL_ALREADY_IN_USE", "email_already_in_use", HttpStatus.CONFLICT);	
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	UserErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.user." + this.messageKey;
	}
}
