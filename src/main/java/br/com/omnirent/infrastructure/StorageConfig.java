package br.com.omnirent.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.com.omnirent.infrastructure.cloudflare.CloudflareProperties;
import br.com.omnirent.infrastructure.cloudflare.R2StorageService;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class StorageConfig {
	
	@Bean
	StorageService storageService(S3Client s3Client, CloudflareProperties properties) {
		
		return new R2StorageService(s3Client, properties);
	}
}
