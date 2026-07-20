package br.com.omnirent.config.global;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record AdminProperties(
        String name,
        String username,
        String email,
        String password
) {}	
