package br.com.omnirent.config.properties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
			StripeProperties.class,
			AppProperties.class
			})
@Configuration
public class PropertiesConfig {}
