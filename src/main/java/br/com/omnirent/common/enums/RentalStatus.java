package br.com.omnirent.common.enums;

public enum RentalStatus {
	RESERVED("Reserved"),
	ACTIVE("Active"),
	RETURNED("Returned"),
	CANCELLED("Cancelled"),
	LATE("Late");
	
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
        throw new IllegalArgumentException("Rental status enumaration not found: " + text);
    }

    public String toString(){
        return rentalStatus;
    }
}
