package br.com.omnirent.exception.common;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.domain.CommonErrorType;
import br.com.omnirent.exception.domain.FieldErrorResponse;
import br.com.omnirent.security.dto.RegisterDTO;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
    private MessageService messageService;

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleException(
        ApiException e, HttpServletRequest request) {
        String localizedMessage = messageService.get(e.getMessageKey(), e.getArgs());

        ApiErrorResponse err = new ApiErrorResponse(
            Instant.now(), e.getHttpStatus().value(),
            e.getErrorType(), e.getErrorCode(), localizedMessage,
            request.getRequestURI()
        );

        return ResponseEntity.status(e.getHttpStatus()).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(
        Exception e, HttpServletRequest request) {

        return handleException(new ApiException(CommonErrorType.INTERNAL_ERROR), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleNotReadable(
        HttpMessageNotReadableException e, HttpServletRequest request) {
        Throwable cause = e.getCause();

        if (cause instanceof InvalidFormatException ex
            && ex.getTargetType().isEnum()) {

            return handleException(
                new ApiException(CommonErrorType.ILLEGAL_ENUMERATION,
                    ex.getValue()),
                request);
        }
        return handleGeneric(e, request);
    }

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
	        MethodArgumentNotValidException ex,
	        HttpServletRequest request) {

	    List<FieldErrorResponse> fields = ex.getBindingResult()
	        .getFieldErrors()
	        .stream()
	        .map(error -> {
	            String fieldCode = getFieldCode(error.getObjectName());

	            String localizedField = messageService.get(
	                fieldCode + error.getField()
	            );

	            Object[] validationArgs = Optional.ofNullable(error.getArguments())
	            	    .orElse(new Object[0]);
	            
	            Object[] args = Stream.concat(
	                Stream.of(localizedField),
	                Arrays.stream(validationArgs, 1, validationArgs.length)
	            ).toArray();

	            String messageKey = "validation.field." + error.getDefaultMessage();

	            String message = messageService.get(messageKey, args);

	            return new FieldErrorResponse(error.getField(), message);
	        })
	        .collect(Collectors.toList());

	    ApiException e = new ApiException(CommonErrorType.VALIDATION_ERROR);

		String localizedMessage = messageService.get(e.getMessageKey());
	    
	    ValidationErrorResponse err = new ValidationErrorResponse(
	        Instant.now(), e.getHttpStatus().value(), e.getErrorType(),
	        e.getErrorCode(), localizedMessage, request.getRequestURI(), fields
	        );

	    return ResponseEntity.status(err.getStatus()).body(err);
	}

	private String getFieldCode(String field) {
		return switch (field) {
		case "registerDTO" -> "user.field.";
		case "userRequestDTO" -> "user.field.";
		case "itemRequestDTO" -> "item.field.";
		case "updateItemRequestDTO" -> "item.field.";
		default -> "";
		};
	}
}
