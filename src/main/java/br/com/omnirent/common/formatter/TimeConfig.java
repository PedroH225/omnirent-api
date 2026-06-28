package br.com.omnirent.common.formatter;

import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeConfig {

    @Value("${app.timezone}")
    private String timezone;

    @Bean
    ZoneId zoneId() {
        return ZoneId.of(timezone);
    }
}
