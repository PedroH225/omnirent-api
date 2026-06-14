package br.com.omnirent.email;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.security.event.UserRegisteredEvent;

@Service
public class EmailService {

	@Autowired
	private EmailSender emailSender;
	
	public void sendWelcomeEmail(UserRegisteredEvent event) {
		EmailMessage message = buildMessage(event);
		
		emailSender.send(message);		
	}

	private EmailMessage buildMessage(UserRegisteredEvent event) {
		String username = event.newUser().username();
		
		if (StringUtils.isBlank(username)) {
			username = "user";
		}
		
		return new EmailMessage(
				event.newUser().email(),
				"Welcome to OmniRent!",
				String.format("Hello %s, welcome to OmniRent!", username));
	}
}
