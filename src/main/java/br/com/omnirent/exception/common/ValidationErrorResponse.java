package br.com.omnirent.exception.common;

import java.time.Instant;
import java.util.List;

import br.com.omnirent.exception.domain.apptype.FieldErrorResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ValidationErrorResponse extends ApiErrorResponse {
	
	private List<FieldErrorResponse> fields;

	public ValidationErrorResponse(Instant timestamp, Integer status, String errorType, String errorCode,
			String message, String path, List<FieldErrorResponse> fields) {
		super(timestamp, status, errorType, errorCode, message, path);
		this.fields = fields;
	}
}
