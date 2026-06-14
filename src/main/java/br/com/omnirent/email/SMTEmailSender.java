package br.com.omnirent.email;

import java.io.UnsupportedEncodingException;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import br.com.omnirent.exception.infrastructure.EmailDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SMTEmailSender implements EmailSender {
	
	private final JavaMailSender javaMailSender;
	
	private final EmailProperties emailProperties;

	@Override
	public void send(EmailMessage message) {
		try {
		    MimeMessage mimeMessage = javaMailSender.createMimeMessage();

		    MimeMessageHelper helper =
		            new MimeMessageHelper(mimeMessage, "UTF-8");

		    helper.setFrom(
		            emailProperties.from(),
		            "OmniRent"
		    );

		    helper.setTo(message.to());
		    helper.setSubject(message.subject());
		    helper.setText(message.body() + message.footer());

		    javaMailSender.send(mimeMessage);

		} catch (MessagingException |
		         UnsupportedEncodingException |
		         MailException e) {

		    throw new EmailDeliveryException(e);
		}
	}
}
