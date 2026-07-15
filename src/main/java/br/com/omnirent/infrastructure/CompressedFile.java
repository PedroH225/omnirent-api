package br.com.omnirent.infrastructure;

public record CompressedFile(
		byte[] bytes,
		String contentType) {}
