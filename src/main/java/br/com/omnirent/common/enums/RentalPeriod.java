package br.com.omnirent.common.enums;

import java.time.temporal.ChronoUnit;

public enum RentalPeriod {
	HOURLY("Hourly", ChronoUnit.HOURS, 0.2),
	DAILY("Daily", ChronoUnit.DAYS, 1.0),
	WEEKLY("Weekly", ChronoUnit.WEEKS, 5.5),
	MONTHLY("Monthly", ChronoUnit.MONTHS, 22);
	
	private final String rentalPeriod;
	
	private final ChronoUnit unit;
	
	private final double multiplier;
	
	RentalPeriod(String rentalPeriod, ChronoUnit unit, double multiplier) {
		this.rentalPeriod = rentalPeriod;
		this.multiplier = multiplier;
		this.unit = unit;
	}
	
	public double getMultiplier() {
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
