package br.com.omnirent.item.context;

import br.com.omnirent.common.enums.ItemCondition;

public record ItemFeedFilter(
		String itemName,
		String categoryName,
		String subCategoryName,
		ItemCondition itemCondition) {

}
