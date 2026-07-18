package br.com.omnirent.infrastructure.ratelimit;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RateLimitConfig {

	@Bean
	Cache<String, ClientRateLimitState> cache() {
	    return Caffeine.newBuilder()
	    		.maximumSize(10_000)
	    		.expireAfterAccess(Duration.ofMinutes(30))
	    		.build();
	}
}
