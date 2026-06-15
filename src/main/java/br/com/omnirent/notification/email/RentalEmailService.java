package br.com.omnirent.notification.email;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.notification.context.RentalNotificationData;

@Service
public class RentalEmailService {
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
	
	public void sendRentalCreatedToOwner(RentalNotificationData notificationData) {
		String messageKey = "rental.created.owner";

		Locale userLocale = Locale.forLanguageTag(notificationData.ownerLocale());
		String username = 
				resolveUsername(notificationData.ownerUsername(), userLocale);
		String itemName = notificationData.itemName();

		EmailMessage message = new EmailMessage(
				notificationData.ownerEmail(),
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(buildBody(messageKey), userLocale, username, itemName),
				buildFooter(userLocale)
				);
		
		emailSender.send(message);
	}

	public void sendRentalCreatedToUser(RentalNotificationData notificationData) {
		String messageKey = "rental.created.renter";

		Locale userLocale = Locale.forLanguageTag(notificationData.renterLocale());
		String username = 
				resolveUsername(notificationData.renterUsername(), userLocale);
		String itemName = notificationData.itemName();

		EmailMessage message = new EmailMessage(
				notificationData.renterEmail(),
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(buildBody(messageKey), userLocale, username, itemName),
				buildFooter(userLocale)
				);
		
		emailSender.send(message);	
	}
}
