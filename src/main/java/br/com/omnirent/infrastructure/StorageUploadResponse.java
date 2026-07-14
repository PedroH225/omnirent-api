package br.com.omnirent.infrastructure;

import java.util.UUID;

public record StorageUploadResponse(
		UUID id,
		String key) {

}
