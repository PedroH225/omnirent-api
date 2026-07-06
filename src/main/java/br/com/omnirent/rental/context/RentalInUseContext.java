package br.com.omnirent.rental.context;

import java.time.Instant;

import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;

public record RentalInUseContext(
	    String id,
	    String ownerId,
	    String renterId,
	    RentalStatus rentalStatus,
	    RentalPeriod rentalPeriod,
	    Instant startDate,
	    Instant endDate
	) {}
