package br.com.omnirent.common.enums;

import java.util.HashSet;
import java.util.Set;

import br.com.omnirent.exception.domain.IllegalEnumerationException;
import br.com.omnirent.exception.domain.IllegalRentalStateException;

public enum RentalStatus {
	CREATED("Created"),
	CONFIRMED("Confirmed"),
	PREPARING("Preparing"),
	SHIPPED("Shipped"),
	IN_USE("In use"),
	RETURN_REQUESTED("Return requested"),
	RETURN_SHIPPED("Return shipped"),
	RETURNED("Returned"),
	CANCELLED("Cancelled"),
	LATE("Late"),
	ACTIVE("Active");
	
	private String rentalStatus;
	
	private Set<RentalStatus> allowedTransitions;
	
	RentalStatus (String rentalStatus){
        this.rentalStatus = rentalStatus;
    }
	
	static {
		CREATED.allowedTransitions = Set.of(CONFIRMED, CANCELLED);
		CONFIRMED.allowedTransitions = Set.of(PREPARING, CANCELLED);
        PREPARING.allowedTransitions = Set.of(SHIPPED);
        SHIPPED.allowedTransitions = Set.of(IN_USE);
        IN_USE.allowedTransitions = Set.of(RETURN_REQUESTED, LATE);
        RETURN_REQUESTED.allowedTransitions = Set.of(RETURN_SHIPPED);
        RETURN_SHIPPED.allowedTransitions = Set.of(RETURNED);
        CANCELLED.allowedTransitions = Set.of();
        LATE.allowedTransitions = Set.of(IN_USE);
    }
    
	public static RentalStatus fromString(String text) {
        for (RentalStatus rentalStatus : RentalStatus.values()) {
            if (rentalStatus.rentalStatus.equalsIgnoreCase(text)) {
                return rentalStatus;
            }
        }
        throw new IllegalEnumerationException(RentalStatus.class, text);
    }
	
	public void validateTransition(RentalStatus target) {
		if (!allowedTransitions.contains(target)) {
			throw new IllegalRentalStateException(this, target);
		}
	}

    public String toString(){
        return rentalStatus;
    }
}
