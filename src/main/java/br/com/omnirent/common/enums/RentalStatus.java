package br.com.omnirent.common.enums;

import java.util.Set;

public enum RentalStatus {
	CREATED,
	CONFIRMED,
	PREPARING,
	SHIPPED,
	IN_USE,
	RETURN_REQUESTED,
	RETURN_SHIPPED,
	RETURNED,
	CANCELLED,
	LATE;
	
	public String getMessageKey() {
		return "rental.status." + name();
	}
	
	private Set<RentalStatus> allowedTransitions;
	
	static {
		CREATED.allowedTransitions = Set.of(CONFIRMED, CANCELLED);
		CONFIRMED.allowedTransitions = Set.of(PREPARING, CANCELLED);
        PREPARING.allowedTransitions = Set.of(SHIPPED);
        SHIPPED.allowedTransitions = Set.of(IN_USE);
        IN_USE.allowedTransitions = Set.of(RETURN_REQUESTED, LATE);
        RETURN_REQUESTED.allowedTransitions = Set.of(RETURN_SHIPPED);
        RETURN_SHIPPED.allowedTransitions = Set.of(RETURNED);
        RETURNED.allowedTransitions = Set.of();
        CANCELLED.allowedTransitions = Set.of();
        LATE.allowedTransitions = Set.of(IN_USE);
    }
	
	public Set<RentalStatus> getAllowedTransitions() {
		return allowedTransitions;
	}
	
	public boolean canTransition(RentalStatus target) {
		return allowedTransitions.contains(target);
	}
}
