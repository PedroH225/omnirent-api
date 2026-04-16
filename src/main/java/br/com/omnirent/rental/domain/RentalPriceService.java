package br.com.omnirent.rental.domain;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.item.domain.Item;

public class RentalPriceService {

	public static BigDecimal calculateFinalPrice(BigDecimal basePrice, RentalPeriod rentalPeriod) {
		return basePrice.multiply(rentalPeriod.getMultiplier());
	}
}
