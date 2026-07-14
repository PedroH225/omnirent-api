package br.com.omnirent.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import br.com.omnirent.infrastructure.cloudflare.CloudflareProperties;

@EnableConfigurationProperties({
			StripeProperties.class,
			AppProperties.class,
			CloudflareProperties.class
			})
@Configuration
public class PropertiesConfig {}
