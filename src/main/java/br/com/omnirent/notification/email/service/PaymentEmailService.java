package br.com.omnirent.notification.email.service;

import java.time.ZoneId;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.config.properties.AppProperties;
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
