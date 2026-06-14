package br.com.omnirent.email;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.omnirent.exception.infrastructure.EmailDeliveryException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SMTEmailSender implements EmailSender {
	
	private final JavaMailSender javaMailSender;
	
	private final EmailProperties emailProperties;

	@Override
	public void send(EmailMessage message) {
		SimpleMailMessage emailMessage = new SimpleMailMessage();
		
		emailMessage.setFrom(emailProperties.from());
		emailMessage.setTo(message.to());
		emailMessage.setSubject(message.subject());
		emailMessage.setText(message.body());

		try {
			javaMailSender.send(emailMessage);
		} catch (MailException e) {
			throw new EmailDeliveryException(e);
		}
	}
}
