package br.com.omnirent.common.enums;

import br.com.omnirent.exception.domain.IllegalEnumerationException;

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
        throw new IllegalEnumerationException(ItemCondition.class, text);
    }
	
	@Override
	public String toString() {
		return this.itemCondition;
	}
}
