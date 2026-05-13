package br.com.omnirent.common.enums;

public enum ItemStatus {
	AVAILABLE,
	UNAVAILABLE,
	BLOCKED;

	public String getMessageKey() {
		return "item.status." + name();
	}
}
