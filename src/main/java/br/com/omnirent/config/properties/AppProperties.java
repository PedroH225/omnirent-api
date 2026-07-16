package br.com.omnirent.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
		String frontUrl,
		String email,
		String locale,
		String timezone,
		DataSize maxFileSize,
		DataSize maxUploadSize) {}
