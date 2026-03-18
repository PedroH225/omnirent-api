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
	
	public static ItemCondition fromString(String text) {
        for (ItemCondition itemCondition : ItemCondition.values()) {
            if (itemCondition.itemCondition.equalsIgnoreCase(text)) {
                return itemCondition;
            }
        }
        throw new IllegalArgumentException("Item condition enumaration not found: " + text);
    }
	
	@Override
	public String toString() {
		return this.itemCondition;
	}
}
