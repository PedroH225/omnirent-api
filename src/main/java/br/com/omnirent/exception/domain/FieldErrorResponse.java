package br.com.omnirent.exception.domain;

public record FieldErrorResponse(
		String field,
		String message
		) {}
