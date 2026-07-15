package br.com.omnirent.exception.domain.apptype;

import org.springframework.http.HttpStatus;

import br.com.omnirent.exception.common.AppErrorType;
import lombok.Getter;

@Getter
public enum ImageErrorType implements AppErrorType {
	FILE_TOO_LARGE("CONTENT_TOO_LARGE", "IMAGE_TOO_LARGE", "too.large", HttpStatus.CONTENT_TOO_LARGE),
	MULTIPART_TOO_LARGE("CONTENT_TOO_LARGE", "MULTIPART_TOO_LARGE", "multipart.too.large", HttpStatus.CONTENT_TOO_LARGE),
	INVALID_IMAGE("UNPROCESSABLE_CONTENT", "INVALID_IMAGE", "invalid", HttpStatus.UNPROCESSABLE_CONTENT),
	UNSUPPORTED_MEDIA_TYPE("UNSUPPORTED_MEDIA_TYPE", "MEDIA_TYPE", "unsupported.media.type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
	MAX_FILES_EXCEEDED("BAD_REQUEST", "MAX_IMAGES_EXCEEDED", "limit.exceeded", HttpStatus.BAD_REQUEST);
	
	private String errorType;
	
	private String errorCode;
	
	private String messageKey;
	
	private HttpStatus httpCode;

	ImageErrorType(String errorType, String errorCode, String messageKey, HttpStatus httpCode) {
		this.errorType = errorType;
		this.errorCode = errorCode;
		this.messageKey = messageKey;
		this.httpCode = httpCode;
	}
	
	@Override
	public String getMessageKey() {
		return "error.image." + messageKey;
	}
}
