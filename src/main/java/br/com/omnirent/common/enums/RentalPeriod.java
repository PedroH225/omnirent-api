package br.com.omnirent.common.enums;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

public enum RentalPeriod {
	HOURLY("Hourly", ChronoUnit.HOURS, new BigDecimal("0.2")),
	DAILY("Daily", ChronoUnit.DAYS, BigDecimal.ONE),
	WEEKLY("Weekly", ChronoUnit.WEEKS, new BigDecimal("5.5")),
	MONTHLY("Monthly", ChronoUnit.MONTHS, new BigDecimal("22"));
	
	private final String rentalPeriod;
	
	private final ChronoUnit unit;
	
	private final BigDecimal multiplier;
	
	RentalPeriod(String rentalPeriod, ChronoUnit unit, BigDecimal multiplier) {
		this.rentalPeriod = rentalPeriod;
		this.multiplier = multiplier;
		this.unit = unit;
	}
	
	public BigDecimal getMultiplier() {
		return this.multiplier;
	}
	
	public ChronoUnit getChronoUnit() {
		return this.unit;
	}
	
	@Override
	public String toString() {
		return this.rentalPeriod;
	}
}
