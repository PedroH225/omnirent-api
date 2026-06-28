package br.com.omnirent.common.enums;

import java.util.Set;

public enum PaymentStatus {
	PENDING,
	PAID,
	FAILED,
	CANCELLED,
	REFUNDED;
	
	private Set<PaymentStatus> allowedTransitions;
	
	static {
		PENDING.allowedTransitions = Set.of(PAID, FAILED, CANCELLED);
		PAID.allowedTransitions = Set.of(REFUNDED);
		FAILED.allowedTransitions = Set.of();
		CANCELLED.allowedTransitions = Set.of();
		REFUNDED.allowedTransitions = Set.of();
	}
	
	public Set<PaymentStatus> getAllowedTransitions() {
		return allowedTransitions;
	}
	
	public boolean canTransition(PaymentStatus targetStatus) {
		return allowedTransitions.contains(targetStatus);
	}

}
