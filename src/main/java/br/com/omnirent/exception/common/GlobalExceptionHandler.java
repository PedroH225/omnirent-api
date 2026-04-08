package br.com.omnirent.exception.common;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(exception = BusinessException.class)
	public ResponseEntity<StandardError> handleException(BusinessException e, HttpServletRequest request) {
        StandardError err = new StandardError
        		(Instant.now(), e.getHttpStatus().value(), e.getError(),
        				e.getMessage(), request.getRequestURI());
        
        return ResponseEntity.status(e.getHttpStatus()).body(err);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<StandardError> handleGeneric(Exception e, HttpServletRequest request) {
	    StandardError err = new StandardError(
	            Instant.now(),
	            500,
	            "Internal server error",
	            "Unexpected error",
	            request.getRequestURI()
	    );

	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
	}
}
