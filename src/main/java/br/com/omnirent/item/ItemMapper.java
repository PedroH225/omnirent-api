package br.com.omnirent.item;

import org.springframework.stereotype.Component;

import br.com.omnirent.address.AddressMapper;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.category.CategoryMapper;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ItemMapper {
	
	private AddressMapper addressMapper;
	
	private CategoryMapper categoryMapper;
	
	private UserMapper userMapper;
	
	public ItemDetailDTO toDto(Item item) {
		ItemData itemData = item.getItemData();
		
		SubCategoryResDTO subCategoryDto = categoryMapper.toSubDto(item.getSubCategory());
		AddressResponseDTO addressDto = addressMapper.toDto(item.getPickupAddress());
		UserResponseDTO ownerDto = userMapper.toDto(item.getOwner());
		
		return new ItemDetailDTO(
			    item.getId(), item.getName(), itemData.getBrand(),
			    itemData.getModel(), itemData.getDescription(), itemData.getBasePrice(),
			    itemData.getItemCondition(), item.getItemStatus(), subCategoryDto,
			    addressDto, ownerDto, item.getCreatedAt(),
			    item.getUpdatedAt()
			);
	}
	
	public ItemCreatedDTO toCreatedDto(Item item) {
		ItemData itemData = item.getItemData();
		SubCategoryResDTO subCategoryDto = categoryMapper.toSubDto(item.getSubCategory());
		AddressResponseDTO addressDto = addressMapper.toDto(item.getPickupAddress());

		return new ItemCreatedDTO(
		        item.getId(), item.getName(), itemData.getBrand(),
		        itemData.getModel(), itemData.getDescription(), itemData.getBasePrice(),
		        itemData.getItemCondition(), item.getItemStatus(), subCategoryDto,
		        addressDto
		);
	}
	
	public ItemSnapshotDTO toSnapshotDTO(ItemSnapshot itemSnapshot) {
		ItemData itemData = itemSnapshot.getItemData();

		return new ItemSnapshotDTO(
			    itemSnapshot.getId(), itemSnapshot.getName(), itemData.getBrand(),
			    itemData.getModel(), itemData.getBasePrice(), itemData.getItemCondition(),
			    itemData.getDescription()
			);
	}

	public Item fromDto(ItemRequestDTO itemDTO, User owner, String ownerId, Address pickUpAddress,
			SubCategory subCategory, ItemStatus itemStatus) {
		Item item = new Item();
		
		item.setName(itemDTO.name());
		ItemData itemData = new ItemData(itemDTO);
		
		item.assignOwner(owner, ownerId);
		item.assignAddress(pickUpAddress);
		item.assignSubCategory(subCategory);
		
		item.setItemStatus(itemStatus);
		
		item.setItemData(itemData);
		
		return item;
	}
	
	public void updateItem(ItemRequestDTO itemDTO, Address address, SubCategory subCategory, Item item) {		
		item.setName(itemDTO.name());
		
		ItemData itemData = new ItemData(itemDTO);
		
		item.setItemData(itemData);
		
		if (address != null) {
			item.assignAddress(address);
		}
		
		if (subCategory != null) {
			item.assignSubCategory(subCategory);
		}
	}
	
	public ItemSnapshot fromRentContext(ItemInfo itemInfo, Rental rental) {
	    ItemSnapshot itemSnapshot = new ItemSnapshot(
	        itemInfo.getItemName(), itemInfo.getBrand(), itemInfo.getModel(),
	        itemInfo.getDescription(), itemInfo.getBasePrice(), itemInfo.getItemCondition()
	    );

	    itemSnapshot.setRental(rental);

	    return itemSnapshot;
	}
	
}
