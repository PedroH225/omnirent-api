package br.com.omnirent.common.enums;

public enum RentalPeriod {
	HOURLY("Hourly",0.2),
	DAILY("Daily", 1.0),
	WEEKLY("Weekly", 5.5),
	MONTHLY("Monthly", 22);
	
	private final String rentalPeriod;
	
	private final double multiplier;
	
	RentalPeriod(String rentalPeriod, double multiplier) {
		this.rentalPeriod = rentalPeriod;
		this.multiplier = multiplier;
	}
	
	public double getMultiplier() {
		return this.multiplier;
	}
	
	@Override
	public String toString() {
		return this.rentalPeriod;
	}
}
