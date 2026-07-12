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
	
	public void sendPaymentConfirmed(
			PaymentNotificationData notificationData) {
		sendDefaultEmail("payment.confirmed", notificationData, notificationData.renterData());
	}
	
	public void sendRefundConfirmed(
			PaymentNotificationData notificationData) {
		sendDefaultEmail("payment.refunded", notificationData, notificationData.renterData());
	}
	
	private void sendPaymentCreatedEmail(
			String messageKey, PaymentNotificationData notificationData, 
			UserNotificationData actorData) {
		Locale userLocale = Locale.forLanguageTag(actorData.locale());
		String username = resolveUsername(actorData.username(), userLocale);
		
		EmailMessage message = new EmailMessage(
				actorData.email(),
				buildSubject(messageKey, userLocale),
				buildBody(messageKey, userLocale, username, 
						notificationData.itemName(),notificationData.amount(), 
						notificationData.currency().toUpperCase(),
						notificationData.provider().getDisplay(),
						notificationData.url()),
				buildFooter(userLocale, true), true
				);

		emailSender.send(message);	
	}
	
	private void sendDefaultEmail(
			String messageKey, PaymentNotificationData notificationData, 
			UserNotificationData actorData) {
		Locale userLocale = Locale.forLanguageTag(actorData.locale());
		String username = resolveUsername(actorData.username(), userLocale);
		
		EmailMessage message = new EmailMessage(
				actorData.email(),
				buildSubject(messageKey, userLocale),
				buildBody(messageKey, userLocale, username, 
						notificationData.itemName(),
						notificationData.provider().getDisplay(),
						notificationData.amount(), 
						notificationData.currency().toUpperCase()),
				buildFooter(userLocale, false), false
				);

		emailSender.send(message);	
	}
	
	private String buildBody(String messageKey, Locale userLocale, Object... args) {
		return messageService.get("email.body." + messageKey, userLocale, args);
	}
	
	private String buildSubject(String messageKey, Locale userLocale) {
		return messageService.get("email.subject." + messageKey, userLocale);
	}
	
	private String buildFooter(Locale locale, boolean isHtlm) {
		if (isHtlm) {
			return messageService.get("email.html.footer", locale, appProperties.frontUrl());
		}
		return messageService.get("email.footer", locale, appProperties.frontUrl());
	}
	
	private String resolveUsername(String username, Locale locale) {
		if (StringUtils.isBlank(username)) {
			return messageService.get("email.to.null.username", locale);
		}
		return username;
	}
}
