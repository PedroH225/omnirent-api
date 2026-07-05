package br.com.omnirent.common.enums;

import java.util.Set;

public enum PaymentStatus {
	PENDING,
	PAID,
	FAILED,
	EXPIRED,
	CANCELLED,
	REFUND_REQUESTED,
	REFUNDED;
	
	private Set<PaymentStatus> allowedTransitions = Set.of();
	
	static {
		PENDING.allowedTransitions = Set.of(PAID, FAILED, CANCELLED, EXPIRED);
		PAID.allowedTransitions = Set.of(REFUND_REQUESTED);
		REFUND_REQUESTED.allowedTransitions = Set.of(REFUNDED);
	}
	
	public Set<PaymentStatus> getAllowedTransitions() {
		return allowedTransitions;
	}
	
	public boolean canTransition(PaymentStatus targetStatus) {
		return allowedTransitions.contains(targetStatus);
	}

}
