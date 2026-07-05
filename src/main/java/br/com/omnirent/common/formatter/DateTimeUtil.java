package br.com.omnirent.common.formatter;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.zone.ZoneRulesException;
import java.util.Locale;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DateTimeUtil {
	
	private final ZoneId defZoneId;
	
	public ZoneId resolveZoneId(String zoneIdStr) {
	    if (zoneIdStr == null || zoneIdStr.isBlank()) {
	        return defZoneId;
	    }

	    try {
	        return ZoneId.of(zoneIdStr);
	    } catch (DateTimeException e) {
	        return defZoneId;
	    }
	}
	
    public String format(ZonedDateTime zdt, Locale locale) {
        DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                .withLocale(locale);

        return zdt.format(formatter);
    }
}
