package br.com.omnirent.email;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.security.event.UserRegisteredEvent;

@Service
public class EmailService {

	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private MessageService messageService;
	
	private static final String OMNI_SITE = "https://omnirent.com";
	
	private String buildBody(String messageKey) {
		return "email.body." + messageKey;
	}
	
	private String buildSubject(String messageKey) {
		return "email.subject." + messageKey;
	}
	
	private String buildTo(String messageKey) {
		return "email.to." + messageKey;
	}
	
	private String buildFooter(Locale locale) {
		return messageService.get("email.footer", locale, OMNI_SITE);
	}
	
	public void sendWelcomeEmail(UserRegisteredEvent event) {
		String messageKey = "welcome";
		Locale userLocale = event.locale();
		String username = event.newUser().username();
		
		if (StringUtils.isBlank(username)) {
			username = messageService.get(buildTo("null.username"), event.locale());
		}
		
		EmailMessage message = new EmailMessage(
				event.newUser().email(),
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(buildBody(messageKey), userLocale, username),
				buildFooter(userLocale)
				);

		emailSender.send(message);		
	}

}
