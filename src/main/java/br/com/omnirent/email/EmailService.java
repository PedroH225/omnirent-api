package br.com.omnirent.email;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.event.UserStatusChangeEvent;
import ch.qos.logback.core.spi.ConfigurationEvent.EventType;

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
	
	private String resolveUsername(String username, Locale locale) {
		if (StringUtils.isBlank(username)) {
			return messageService.get(buildTo("null.username"), locale);
		}
		return username;
	}
	
	public void sendWelcomeEmail(UserRegisteredEvent event) {
		String messageKey = "welcome";
		Locale userLocale = event.locale();
		String username = resolveUsername(event.newUser().username(), userLocale);
		
		EmailMessage message = new EmailMessage(
				event.newUser().email(),
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(buildBody(messageKey), userLocale, username),
				buildFooter(userLocale)
				);

		emailSender.send(message);		
	}

	public void sendUserStatusChanged(UserStatusChangeEvent event) {
		UserStatus newStatus = event.newStatus();
		Locale userLocale = event.locale();
		String username = resolveUsername(event.username(), userLocale);
				
		EmailMessage message = new EmailMessage(
				event.email(),
				messageService.get(buildSubject(newStatus.getMessageKey() ), userLocale),
				messageService.get(buildBody(newStatus.getMessageKey()), userLocale, username),
				buildFooter(userLocale)
				);

		emailSender.send(message);
	}

}
