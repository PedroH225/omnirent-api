package br.com.omnirent.exception.domain.apptype;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record FieldErrorResponse(
		String field,
		String message,
		@JsonIgnore
		Integer priority
		) {}
