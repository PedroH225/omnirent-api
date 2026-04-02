package br.com.omnirent.rental.domain;

import java.time.LocalDateTime;

import br.com.omnirent.common.enums.RentalPeriod;

public class RentalDateService {

	public static LocalDateTime calculateEndDate(LocalDateTime startDate, RentalPeriod rentalPeriod) {
		return startDate.plus(1, rentalPeriod.getChronoUnit());
	}
}
