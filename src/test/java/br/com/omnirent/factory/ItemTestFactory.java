package br.com.omnirent.factory;

import java.math.BigDecimal;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.Sequence;

public final class ItemTestFactory {

    private ItemTestFactory() {}

    public static Item create(
            User owner, Address address, SubCategory subCategory,
            String price, ItemCondition condition
    ) {
    	String itemStr = Sequence.nextString("itemdata-");
        ItemData data = new ItemData(
        	itemStr, itemStr, itemStr,
            new BigDecimal(price), condition
        );

        Item item = new Item();
        item.setName(itemStr);
        item.setItemData(data);
        item.setItemStatus(ItemStatus.AVAILABLE);
        item.setOwnerId(owner.getId());
        item.setSubCategoryId(subCategory.getId());
        item.setPickupAddressId(address.getId());
        return item;
    }
}
