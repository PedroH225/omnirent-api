package br.com.omnirent.factory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import br.com.omnirent.address.context.AddressInfo;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.item.context.ChangeItemAddressContext;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.context.UpdateItemContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserResponseDTO;
import br.com.omnirent.utils.Sequence;

public final class ItemTestFactory {

    private ItemTestFactory() {}

    public static Item create(
            User owner, Address address, SubCategory subCategory,
            String price, ItemCondition condition
    ) {
    	String itemStr = Sequence.nextString("itemdata");
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
    
    public static Item createPersisted(User owner, Address address, SubCategory subCategory,
            String price, ItemCondition condition) {
    	Item item = create(owner, address, subCategory, price, condition);
    	item.setId(Sequence.nextString("itemId"));
    	item.setCreatedAt(LocalDateTime.now());
    	item.setUpdatedAt(LocalDateTime.now());
    	if (owner.getItems() == null) {
			owner.setItems(new ArrayList<Item>());
		}
    	owner.getItems().add(item);
    	return item;
    }
    
    public static ItemDetailDTO toItemDetailsDto(Item item, SubCategory subCategory,
            Address pickupAddress, User owner) {

        ItemData itemData = item.getItemData();

        return new ItemDetailDTO(
                item.getId(), item.getName(), itemData.getBrand(),
                itemData.getModel(), itemData.getDescription(), itemData.getBasePrice(),
                itemData.getItemCondition(), item.getItemStatus(),
                SubCategoryTestFactory.toSubDto(subCategory),
                AddressTestFactory.toAddressDto(pickupAddress),
                new UserResponseDTO(
                        owner.getId(), owner.getUsername()
                ),
                item.getCreatedAt(), item.getUpdatedAt()
        );
    }
    
    public static ItemRentedContext toItemRentedContext(Item item,
            Address address, User owner) {
        ItemData itemData = item.getItemData();
        AddressData addressData = address.getAddressData();

        ItemInfo itemInfo = new ItemInfo(
                item.getId(), item.getName(), itemData.getBrand(),
                itemData.getModel(), itemData.getDescription(), itemData.getBasePrice(),
                itemData.getItemCondition()
        );
        
        AddressInfo addressInfo = new AddressInfo(
                address.getId(), addressData.getStreet(), addressData.getNumber(),
                addressData.getComplement(), addressData.getDistrict(), addressData.getCity(),
                addressData.getState(), addressData.getCountry(), addressData.getZipCode()
        );

        return new ItemRentedContext(
                itemInfo, addressInfo, owner.getId(),
                owner.getUsername()
        );
    }
    
    public static ItemDisplayDTO toItemDisplayDTO(Item item,
            SubCategory subCategory, User owner) {
        ItemData itemData = item.getItemData();

        return new ItemDisplayDTO(
                item.getId(), item.getName(), itemData.getBasePrice(),
                itemData.getItemCondition(), item.getItemStatus(), subCategory.getName(),
                item.getCreatedAt(),
                new UserResponseDTO(
                        owner.getId(), owner.getUsername()
                )
        );
    }
    
    public static ItemRequestDTO createItemRequest(String itemId, String basePrice, String itemCondition,
    		String subCategoryId, String addressId) {
    	String item = Sequence.nextString("item");
    	
    
        return new ItemRequestDTO(
                itemId, item, item,
                item, item, new BigDecimal(basePrice),
                itemCondition, subCategoryId,
                addressId
        );
    }
    
    public static UpdateItemRequestDTO updateItemRequest(String itemId, String basePrice, String itemCondition) {
    	String item = Sequence.nextString("item");
    	
        return new UpdateItemRequestDTO(
                itemId, item, item,
                item, item, new BigDecimal(basePrice),
                itemCondition
        );
    }
    
    public static UpdateItemContext updateItemContext(Item item, String ownerId) {
        ItemData data = item.getItemData();

        ItemInfo itemInfo = new ItemInfo(
            item.getId(),
            item.getName(),
            data.getBrand(),
            data.getModel(),
            data.getDescription(),
            data.getBasePrice(),
            data.getItemCondition()
        );

        return new UpdateItemContext(itemInfo, ownerId, item.getItemStatus());
    }
    
    public static Item fromNewItemRequestDTO(ItemRequestDTO dto,
            SubCategory subCategory, Address address, User owner) {

        ItemData itemData = new ItemData(
                dto.brand(), dto.model(), dto.description(),
                dto.basePrice(), ItemCondition.valueOf(dto.itemCondition())
        );

        Item item = new Item();
        item.setName(dto.name());
        item.setItemData(itemData);
        item.setItemStatus(ItemStatus.AVAILABLE);
        item.setSubCategory(subCategory);
        item.setSubCategoryId(subCategory.getId());
        item.setPickupAddress(address);
        item.setPickupAddressId(address.getId());
        item.setOwner(owner);
        item.setOwnerId(owner.getId());

        return item;
    }
    
    public static Item toPersisted(Item item) {
    	item.setId(Sequence.nextString("item-id"));
    	item.setCreatedAt(LocalDateTime.now());
    	item.setUpdatedAt(LocalDateTime.now());
    	return item;
    }
    
    public static ItemCreatedDTO toItemCreatedDTO(Item item) {
        ItemData itemData = item.getItemData();

        return new ItemCreatedDTO(
                item.getId(), item.getName(), itemData.getBrand(),
                itemData.getModel(), itemData.getDescription(), itemData.getBasePrice(),
                itemData.getItemCondition(), item.getItemStatus());
    }
    
    public static ChangeItemAddressContext toChangeAddressContext(Item item) {
    	return new ChangeItemAddressContext(item.getId(), item.getOwnerId(),
    			item.getPickupAddressId(), item.getItemStatus());
    }
}
