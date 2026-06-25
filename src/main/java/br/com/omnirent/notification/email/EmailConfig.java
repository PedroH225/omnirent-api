package br.com.omnirent.notification.email;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@EnableConfigurationProperties(EmailProperties.class)
public class EmailConfig {

	@Bean
	EmailSender emailSender(JavaMailSender javaMailSender,
            EmailProperties emailProperties) {

		return new SMTEmailSender(javaMailSender, emailProperties);
	}
}
