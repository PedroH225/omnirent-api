package br.com.omnirent.email;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.infrastructure.NotificationDataNotException;
import br.com.omnirent.rental.event.RentalCreatedEvent;
import br.com.omnirent.user.UserQueryRepository;
import br.com.omnirent.user.context.UserNotificationData;

@Service
public class RentalEmailService {
	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserQueryRepository queryRepository;
	
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
	
	public void sendRentalCreatedToOwner(RentalCreatedEvent event) {
		String messageKey = "rental.created.owner";
		UserNotificationData notificationData = 
				queryRepository.findNotificationData(event.data().ownerId())
				.orElseThrow(() -> new NotificationDataNotException());

		Locale userLocale = Locale.forLanguageTag(notificationData.locale());
		String username = 
				resolveUsername(notificationData.username(), userLocale);
		String email = notificationData.email();
		String itemName = event.data().item().itemName();

		EmailMessage message = new EmailMessage(
				email,
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(buildBody(messageKey), userLocale, username, itemName),
				buildFooter(userLocale)
				);
		
		emailSender.send(message);
	}
}
