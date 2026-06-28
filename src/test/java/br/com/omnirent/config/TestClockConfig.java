package br.com.omnirent.config;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestClockConfig {

    @Bean
    Clock clock() {
        return Clock.fixed(
            Instant.parse("2026-06-28T10:00:00Z"),
            ZoneOffset.UTC
        );
    }
}