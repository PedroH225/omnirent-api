package br.com.omnirent.common.enums;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;

public enum RentalPeriod {
	HOURLY(ChronoUnit.HOURS, new BigDecimal("0.2")),
	DAILY(ChronoUnit.DAYS, BigDecimal.ONE),
	WEEKLY(ChronoUnit.WEEKS, new BigDecimal("5.5")),
	MONTHLY(ChronoUnit.MONTHS, new BigDecimal("22"));
		
	private final ChronoUnit unit;
	
	private final BigDecimal multiplier;
	
	RentalPeriod(ChronoUnit unit, BigDecimal multiplier) {
		this.multiplier = multiplier;
		this.unit = unit;
	}
	
	public String getMessageKey() {
		return "rental.period." + name();
	}
	
	public BigDecimal getMultiplier() {
		return this.multiplier;
	}
	
	public ChronoUnit getChronoUnit() {
		return this.unit;
	}
}
