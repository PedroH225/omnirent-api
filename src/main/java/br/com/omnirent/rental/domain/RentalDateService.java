package br.com.omnirent.rental.domain;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalPeriod;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class RentalDateService {

	private final ZoneId zoneId;
	
	public Instant calculateEndDate(Instant startDate, RentalPeriod rentalPeriod) {
		ZonedDateTime zonedStartDate = ZonedDateTime.ofInstant(startDate, zoneId);
		ZonedDateTime zonedEndDate = zonedStartDate.plus(1, rentalPeriod.getChronoUnit());
		return zonedEndDate.toInstant();
	}
}
