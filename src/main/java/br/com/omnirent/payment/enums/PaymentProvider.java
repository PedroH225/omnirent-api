package br.com.omnirent.payment.enums;

public enum PaymentProvider {
	STRIPE;
	
	public String getDisplay() {
		String value = this.name().toLowerCase();
		return value.substring(0, 1).toUpperCase() + value.substring(1);
	}
}
