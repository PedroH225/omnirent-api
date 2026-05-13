package br.com.omnirent.config.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MessageService {

	private final MessageSource messageSource;
	
	public String get(String key, Object... args) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(key, args, locale);
	}
}
