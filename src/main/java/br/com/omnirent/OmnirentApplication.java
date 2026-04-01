package br.com.omnirent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OmnirentApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmnirentApplication.class, args);
	}

}
