package br.com.omnirent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
public class OmnirentApplication {
	
    private static final Logger log = LoggerFactory.getLogger(OmnirentApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OmnirentApplication.class, args);
		log.info("Application started.");
	}
}
