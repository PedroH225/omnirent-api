package br.com.omnirent.common.enums;

public enum UserStatus {
	ACTIVE,
	INACTIVE,
	BANNED;
	
	public String getMessageKey() {
		return "user.status." + name(); 
	}
}
