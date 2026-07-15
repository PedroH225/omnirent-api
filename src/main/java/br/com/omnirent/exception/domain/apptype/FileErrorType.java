package br.com.omnirent.exception.domain.apptype;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum FileErrorType implements AppErrorType {
	FILE_TOO_LARGE("CONTENT_TOO_LARGE", "FILE_TOO_LARGE", "too.large", HttpStatus.CONTENT_TOO_LARGE),
	MULTIPART_TOO_LARGE("CONTENT_TOO_LARGE", "MULTIPART_TOO_LARGE", "multipart.too.large", HttpStatus.CONTENT_TOO_LARGE);
	
	private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;

	FileErrorType(String errorType, String errorCode, String messageKey, HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.file." + messageKey;
	}
}
