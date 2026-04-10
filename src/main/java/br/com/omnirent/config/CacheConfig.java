package br.com.omnirent.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig {
	@Bean
	public CacheManager cacheManager() {
	    CaffeineCacheManager manager = new CaffeineCacheManager("tokenVersion");
	    manager.setCaffeine(
	        Caffeine.newBuilder()
	            .maximumSize(10_000)
	            .expireAfterWrite(30, TimeUnit.MINUTES)
	    );
	    return manager;
	}
}
