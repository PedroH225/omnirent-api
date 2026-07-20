package br.com.omnirent.notification.email.service;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.ItemRejectionReason;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.infrastructure.NotificationDataNotException;
import br.com.omnirent.item.context.ItemAuditSnapshot;
import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.item.event.ItemRejectedEvent;
import br.com.omnirent.notification.JpaNotificationQueryRepository;
import br.com.omnirent.notification.context.AdminNotificationData;
import br.com.omnirent.notification.context.ItemNotificationData;
import br.com.omnirent.notification.context.UserNotificationData;
import br.com.omnirent.notification.email.EmailMessage;
import br.com.omnirent.notification.email.EmailSender;

@Service
public class ItemEmailService {	

	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private JpaNotificationQueryRepository queryRepository;
	
	@Autowired
	private AppProperties appProperties;
	
	private String buildBody(String messageKey, Locale locale, Object... args) {
		return messageService.get("email.body." + messageKey, locale, args);
	}
	
	private String buildSubject(String messageKey, Locale locale) {
		return messageService.get("email.subject." + messageKey, locale);
	}
	
	private String buildTo(String messageKey) {
		return "email.to." + messageKey;
	}
	
	private String buildFooter(Locale locale) {
		return messageService.get("email.footer", locale, appProperties.frontUrl());
	}
	
	private String resolveUsername(String username, Locale locale) {
		if (StringUtils.isBlank(username)) {
			return messageService.get(buildTo("null.username"), locale);
		}
		return username;
	}
	
	public void sendNewItemEmail(ItemCreatedEvent event) {
		String messageKey = "new_item";
		UserNotificationData notificationData = 
				queryRepository.findNotificationData(event.actorId())
				.orElseThrow(() -> new NotificationDataNotException());
	
		Locale userLocale = Locale.forLanguageTag(notificationData.locale());
		String username = 
				resolveUsername(notificationData.username(), userLocale);
		String email = notificationData.email();
		String itemName = event.currentBody().itemName();
		
		EmailMessage message = new EmailMessage(
				email,
				buildSubject(messageKey, userLocale),
				buildBody(messageKey, userLocale, username, itemName),
				buildFooter(userLocale)
				);

		emailSender.send(message);
	}

	public void notifyAdmin(ItemCreatedEvent event, AdminNotificationData adminData) {
		String messageKey = "admin.new_item";
		ItemAuditSnapshot item = event.currentBody();
		Locale admLocale = Locale.forLanguageTag(adminData.locale());
		String email = adminData.email();
		
		String redirectUrl = 
				String.format("%s/review/%s", appProperties.frontUrl(), event.entityId());

		EmailMessage message = new EmailMessage(
				email,
				buildSubject(messageKey, admLocale),
				buildBody(messageKey, admLocale, item.itemName(), redirectUrl),
				buildFooter(admLocale)
				);

		emailSender.send(message);
	}

	public void sendItemRejectedEmail(ItemRejectedEvent event, ItemNotificationData notificationData) {
		String messageKey = "item.rejected";
		ItemRejectionReason reason = event.currentBody().reason();
		
		UserNotificationData userData = notificationData.ownerData();
		Locale userLocale = Locale.forLanguageTag(userData.locale());
		String username = 
				resolveUsername(userData.username(), userLocale);
		
		String reasonDescription =
				messageService.get(reason.getDescriptionKey(), userLocale);
		
		EmailMessage message = new EmailMessage(
				userData.email(),
				buildSubject(messageKey, userLocale),
				buildBody(messageKey, userLocale, 
						username, notificationData.name(), reasonDescription),
				buildFooter(userLocale)
				);
		
		emailSender.send(message);
	}

}
