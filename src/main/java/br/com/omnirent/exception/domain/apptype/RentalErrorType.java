package br.com.omnirent.exception.domain.apptype;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum RentalErrorType implements AppErrorType {

    NOT_FOUND("NOT_FOUND", "RENTAL_NOT_FOUND", "not_found", HttpStatus.NOT_FOUND),
    ILLEGAL_STATE_TRANSITION("CONFLICT", "ILLEGAL_RENTAL_STATE_TRANSITION",
    		"illegal_state_transition", HttpStatus.CONFLICT),
    CREATION_COOLDOWN("CONFLICT", "RENTAL_CREATION_COOLDOWN", "creation_cooldown", HttpStatus.CONFLICT),
    OPERATION_FORBIDDEN("FORBIDDEN", "RENTAL_OPERATION_FORBIDDEN", "operation_forbidden", HttpStatus.FORBIDDEN);
	
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	RentalErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.rental." + this.messageKey;
	}
}
