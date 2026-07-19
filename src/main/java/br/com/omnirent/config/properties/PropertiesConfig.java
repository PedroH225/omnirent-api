package br.com.omnirent.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import br.com.omnirent.infrastructure.cloudflare.CloudflareProperties;
import br.com.omnirent.infrastructure.ratelimit.RateLimitProperties;

@EnableConfigurationProperties({
			StripeProperties.class,
			AppProperties.class,
			CloudflareProperties.class,
			RateLimitProperties.class
			})
@Configuration
public class PropertiesConfig {}
