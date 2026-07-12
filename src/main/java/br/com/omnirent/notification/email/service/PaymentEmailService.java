package br.com.omnirent.notification.email.service;

import java.time.ZoneId;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.notification.context.PaymentNotificationData;
import br.com.omnirent.notification.context.UserNotificationData;
import br.com.omnirent.notification.email.EmailMessage;
import br.com.omnirent.notification.email.EmailSender;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentEmailService {

	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private ZoneId zoneId;
	
	@Autowired
	private AppProperties appProperties;
	
	public void sendPaymentCreated(
			PaymentNotificationData notificationData) {
		sendPaymentCreatedEmail("payment.created", notificationData, notificationData.renterData());
	}
	
	private void sendPaymentCreatedEmail(
			String messageKey, PaymentNotificationData notificationData, 
			UserNotificationData actorData) {
		Locale userLocale = Locale.forLanguageTag(actorData.locale());
		String username = resolveUsername(actorData.username(), userLocale);
		
		EmailMessage message = new EmailMessage(
				actorData.email(),
				messageService.get(buildSubject(messageKey), userLocale),
				messageService.get(
						buildBody(messageKey), userLocale, username, 
						notificationData.itemName(),notificationData.amount(), 
						notificationData.currency().toUpperCase(),
						notificationData.provider().getDisplay(),
						notificationData.url()),
				buildFooter(userLocale)
				);

		emailSender.send(message);	
	}
	
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
		return messageService.get("email.footer", locale, appProperties.frontUrl());
	}
	
	private String resolveUsername(String username, Locale locale) {
		if (StringUtils.isBlank(username)) {
			return messageService.get(buildTo("null.username"), locale);
		}
		return username;
	}
}
