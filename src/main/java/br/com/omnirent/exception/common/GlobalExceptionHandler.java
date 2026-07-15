package br.com.omnirent.exception.common;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.common.enums.FieldErrorPriority;
import br.com.omnirent.common.formatter.CaptalizationUtils;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.domain.apptype.CommonErrorType;
import br.com.omnirent.exception.domain.apptype.FieldErrorResponse;
import br.com.omnirent.exception.domain.apptype.FileErrorType;
import br.com.omnirent.exception.domain.apptype.ImageErrorType;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.security.dto.RegisterDTO;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserRequestDTO;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@Autowired
	private AppProperties appProperties;

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
                    ex.getValue(), 
                    Arrays.toString(ex.getTargetType().getEnumConstants())),
                request);
        }
        return handleGeneric(e, request);
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException e,
            HttpServletRequest request) {

        if (e.getRequiredType() != null && e.getRequiredType().isEnum()) {
            return handleException(
                    new ApiException(
                            CommonErrorType.ILLEGAL_ENUMERATION,
                            e.getValue(), 
                            Arrays.toString(e.getRequiredType().getEnumConstants())
                    ),
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
	            String fieldCode = getFieldCode(ex.getObjectName());
	            
	            FieldErrorPriority errorPriority = FieldErrorPriority.fromSpringCode(error.getCode());

	            String localizedField = messageService.get(
	                fieldCode + error.getField()
	            );

	            Object[] validationArgs = Optional.ofNullable(error.getArguments())
	            	    .orElse(new Object[0]);
	            
	            Object[] args = Stream.concat(
	                Stream.of(localizedField),
	                Arrays.stream(validationArgs).skip(1)
	            ).toArray();

	            String messageKey = "validation.field." + error.getDefaultMessage();

	            String message = messageService.get(messageKey, args);

	            return new FieldErrorResponse(error.getField(), message, errorPriority.ordinal());
	        })
	        .collect(Collectors.toList());

	    List<FieldErrorResponse> filteredErrors = filterBestError(fields);
	    
	    ApiException e = new ApiException(CommonErrorType.VALIDATION_ERROR);

		String localizedMessage = messageService.get(e.getMessageKey());
	    
	    ValidationErrorResponse err = new ValidationErrorResponse(
	        Instant.now(), e.getHttpStatus().value(), e.getErrorType(),
	        e.getErrorCode(), localizedMessage, request.getRequestURI(), filteredErrors
	        );

	    return ResponseEntity.status(err.getStatus()).body(err);
	}
	
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ValidationErrorResponse> handleValidationException(
	        ValidationException ex,
	        HttpServletRequest request
	) {
		String localizedErrorMessage = messageService.get(ex.getMessageKey(), ex.getArgs());

	    List<FieldErrorResponse> localizedFields = ex.getFields()
	        .stream()
	        .map(field -> {
	            String fieldCode = getFieldCode(ex.getObjectName());
	            
	            String localizedField = messageService.get(fieldCode + field.field());

	            String localizedMessage = messageService.get(field.message(), localizedField);
	            
	            return new FieldErrorResponse(
	                field.field(),
	                CaptalizationUtils.firstCaptalizedOnly(localizedMessage),
	                null
	            );
	        })
	        .collect(Collectors.toList());

	    ValidationErrorResponse err =
	        new ValidationErrorResponse(
	            Instant.now(), ex.getHttpStatus().value(), ex.getErrorType(),
	            ex.getErrorCode(), localizedErrorMessage, request.getRequestURI(),
	            localizedFields
	        );

	    return ResponseEntity.status(err.getStatus()).body(err);
	}
	
	private List<FieldErrorResponse> filterBestError(List<FieldErrorResponse> fieldErrors) {
	    Map<String, FieldErrorResponse> bestByField = new HashMap<>();

	    for (FieldErrorResponse error : fieldErrors) {

	        bestByField.merge(
	                error.field(),
	                error,
	                (existing, incoming) ->
	                        incoming.priority() < existing.priority()
	                                ? incoming
	                                : existing
	        );
	    }

	    return new ArrayList<>(bestByField.values());
	}
	
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiErrorResponse> handleMaxUploadSize(
	        MaxUploadSizeExceededException e,
	        HttpServletRequest request) {
		if (hasCause(e, FileSizeLimitExceededException.class)) {
	        return handleException(
	                new ApiException(
	                        FileErrorType.FILE_TOO_LARGE,
	                        appProperties.maxFileSize().toMegabytes()), request);
		} 
		return handleException(
				new ApiException(FileErrorType.MULTIPART_TOO_LARGE, 
						appProperties.maxUploadSize().toMegabytes()), request);	
	}

	private String getFieldCode(String objectName) {
		String normalizedName = StringUtils.capitalize(objectName);
		
		if (normalizedName.equals(RegisterDTO.class.getSimpleName()) ||
			normalizedName.equals(UserRequestDTO.class.getSimpleName()) ||
			normalizedName.equals(User.class.getSimpleName())) {
			return "user.field.";
		}
		if (normalizedName.equals(ItemRequestDTO.class.getSimpleName()) ||
			normalizedName.equals(UpdateItemRequestDTO.class.getSimpleName())) {
			return "item.field.";
		}
		
		if (normalizedName.equals(AddressRequestDTO.class.getSimpleName())) {
			return "address.field.";
		}
		return "";
	}
	
	private boolean hasCause(Throwable ex, Class<? extends Throwable> type) {
	    while (ex != null) {
	        if (type.isInstance(ex)) {
	            return true;
	        }
	        ex = ex.getCause();
	    }
	    return false;
	}
}
