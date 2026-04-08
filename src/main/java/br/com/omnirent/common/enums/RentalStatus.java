package br.com.omnirent.common.enums;

import br.com.omnirent.exception.domain.IllegalEnumerationException;

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
	
	RentalStatus (String rentalStatus){
        this.rentalStatus = rentalStatus;
    }
    
	public static RentalStatus fromString(String text) {
        for (RentalStatus rentalStatus : RentalStatus.values()) {
            if (rentalStatus.rentalStatus.equalsIgnoreCase(text)) {
                return rentalStatus;
            }
        }
        throw new IllegalEnumerationException(RentalStatus.class, text);
    }

    public String toString(){
        return rentalStatus;
    }
}
