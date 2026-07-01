package br.com.omnirent.common.enums;

import java.util.Set;

public enum PaymentStatus {
	PENDING,
	PAID,
	FAILED,
	CANCELLED,
	REFUND_REQUESTED,
	REFUNDED;
	
	private Set<PaymentStatus> allowedTransitions;
	
	static {
		PENDING.allowedTransitions = Set.of(PAID, FAILED, CANCELLED);
		PAID.allowedTransitions = Set.of(REFUND_REQUESTED);
		FAILED.allowedTransitions = Set.of();
		CANCELLED.allowedTransitions = Set.of();
		REFUND_REQUESTED.allowedTransitions = Set.of();
		REFUNDED.allowedTransitions = Set.of(REFUNDED);
	}
	
	public Set<PaymentStatus> getAllowedTransitions() {
		return allowedTransitions;
	}
	
	public boolean canTransition(PaymentStatus targetStatus) {
		return allowedTransitions.contains(targetStatus);
	}

}
