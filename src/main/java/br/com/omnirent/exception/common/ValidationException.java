package br.com.omnirent.exception.common;

import java.util.List;

import br.com.omnirent.exception.domain.apptype.FieldErrorResponse;
import lombok.Getter;

@Getter
public class ValidationException extends ApiException {
	private static final long serialVersionUID = 1L;
	
	private final List<FieldErrorResponse> fields;
	
	private String objectName;
		
	public ValidationException(AppErrorType appErrorType, List<FieldErrorResponse> fields,
			String objectName) {
		    super(appErrorType);
		    this.fields = List.copyOf(fields);
		    this.objectName = objectName;
		}

}
