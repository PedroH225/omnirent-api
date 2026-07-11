package br.com.omnirent.exception.domain.apptype;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum AuthenticationErrorType implements AppErrorType {

	AUTHENTICATION_SERVICE_ERROR("INTERNAL_SERVER_ERROR", "AUTHENTICATION_SERVICE_ERROR", "authentication_service_error", HttpStatus.INTERNAL_SERVER_ERROR),
	OAUTH_PROVIDER_REQUIRED("BAD_REQUEST", "OAUTH_PROVIDER_REQUIRED", "oauth_provider_required", HttpStatus.BAD_REQUEST),
	UNSUPPORTED_AUTH_PROVIDER("BAD_REQUEST", "UNSUPPORTED_AUTH_PROVIDER", "unsupported_auth_provider", HttpStatus.BAD_REQUEST),
	OAUTH_SUB_REQUIRED("BAD_REQUEST", "OAUTH_SUB_REQUIRED", "oauth_sub_required", HttpStatus.BAD_REQUEST),
	OAUTH_EMAIL_REQUIRED("BAD_REQUEST", "OAUTH_EMAIL_REQUIRED", "oauth_email_required", HttpStatus.BAD_REQUEST),
	OAUTH_AUTHENTICATION_FAILED("UNAUTHORIZED", "OAUTH_AUTHENTICATION_FAILED", "oauth_authentication_failed", HttpStatus.UNAUTHORIZED),
    OAUTH_PROVIDER_UNAVAILABLE("SERVICE_UNAVAILABLE", "OAUTH_PROVIDER_UNAVAILABLE", "oauth_unavailable", HttpStatus.SERVICE_UNAVAILABLE),
	OAUTH_ACCESS_DENIED("UNAUTHORIZED", "OAUTH_ACCESS_DENIED", "oauth_access_denied", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("UNAUTHORIZED", "INVALID_TOKEN", "authentication_required", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_REQUIRED("UNAUTHORIZED", "AUTHENTICATION_REQUIRED", "authentication_required", HttpStatus.UNAUTHORIZED),
    INVALID_CREDENTIALS("UNAUTHORIZED", "INVALID_CREDENTIALS", "invalid_credentials", HttpStatus.UNAUTHORIZED);
    	
    private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;
	
	AuthenticationErrorType(String errorType, String errorCode, String messageKey,
			HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.auth." + this.messageKey;
	}
}
