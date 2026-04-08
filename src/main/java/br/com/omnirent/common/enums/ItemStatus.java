package br.com.omnirent.common.enums;

import br.com.omnirent.exception.domain.IllegalEnumerationException;

public enum ItemStatus {
    AVAILABLE("Available"),
    UNAVAILABLE("Unavailable"),
    RENTED("Rented"),
    INACTIVE("Inactive"),
    BLOCKED("Blocked");

    private String itemStatus;

    ItemStatus (String itemStatus){
        this.itemStatus = itemStatus;
    }
    
	public static ItemStatus fromString(String text) {
        for (ItemStatus itemStatus : ItemStatus.values()) {
            if (itemStatus.itemStatus.equalsIgnoreCase(text)) {
                return itemStatus;
            }
        }
        throw new IllegalEnumerationException(ItemStatus.class, text);
    }

    public String toString(){
        return itemStatus;
    }
}
