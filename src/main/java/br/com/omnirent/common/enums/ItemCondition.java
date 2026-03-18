package br.com.omnirent.common.enums;

public enum ItemCondition {
	NEW("New"),
	LIKE_NEW("Like new"),
	GOOD("Good"),
	USED("Used"),
	HEAVILY_USED("Heavily used");
	
	private String itemCondition;
	
	private ItemCondition(String itemCondition) {
		this.itemCondition = itemCondition;
	}
	
	@Override
	public String toString() {
		return this.itemCondition;
	}
}
