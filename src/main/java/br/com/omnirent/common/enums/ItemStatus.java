package br.com.omnirent.common.enums;

public enum ItemStatus {
	ANALISYS,
	AVAILABLE,
	RENTED,
	UNAVAILABLE,
	BLOCKED;
	
	public String getMessageKey() {
		return "item.status." + name();
	}
}
