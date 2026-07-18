package br.com.omnirent.infrastructure.ratelimit;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate.limit")
public record RateLimitProperties(
        Duration window,
        List<Duration> penalties
) {}
