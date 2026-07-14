package br.com.omnirent.infrastructure.cloudflare;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "r2")
public record CloudflareProperties(
		String endpoint, 
		String accessKey, 
		String secretKey, 
		String bucket
		) {}
