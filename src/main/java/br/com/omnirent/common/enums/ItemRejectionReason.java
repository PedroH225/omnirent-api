package br.com.omnirent.common.enums;

public enum ItemRejectionReason {

    INVALID_TITLE,
    INVALID_DESCRIPTION,
    INVALID_CATEGORY,

    LOW_QUALITY_IMAGES,
    INVALID_IMAGES,

    PROHIBITED_ITEM,
    COUNTERFEIT_ITEM,
    INAPPROPRIATE_CONTENT,

    DUPLICATE_ITEM,

    INCOMPLETE_INFORMATION,

    OTHER;

	public String getLabelKey() {
		return "item.rejected.reason." + name();
	}
	
	public String getDescriptionKey() {
		return "item.rejected.reason.description" + name();
	}
	
}
