package br.com.omnirent.exception.common;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.domain.CommonErrorType;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@Autowired
	private MessageService messageService;
			
	@ExceptionHandler(exception = ApiException.class)
	public ResponseEntity<ApiErrorResponse> handleException(ApiException e, HttpServletRequest request) {
	    String localizedMessage = messageService.get(e.getMessageKey(), e.getArgs());

	    ApiErrorResponse err = new ApiErrorResponse(
	            Instant.now(),
	            e.getHttpStatus().value(),
	            e.getErrorType(),
	            e.getErrorCode(),
	            localizedMessage,
	            request.getRequestURI()
	    );
        
        return ResponseEntity.status(e.getHttpStatus()).body(err);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGeneric(Exception e, HttpServletRequest request) {
	    return handleException(new ApiException(CommonErrorType.INTERNAL_ERROR), request);
	}
}
