package br.com.omnirent.common.enums;

public enum UserStatus {
	ACTIVE("Active"),
	INACTIVE("Inactive"),
	BANNED("Banned");
	
	private String userStatus;
	
	UserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	
	@Override
	public String toString() {
		return this.userStatus;
	}
}
