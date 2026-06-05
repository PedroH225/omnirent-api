package br.com.omnirent.item;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.omnirent.address.AddressMapper;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.category.CategoryMapper;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.common.enums.EnumOption;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemEnums;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.item.context.ItemAuditSnapshot;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.UpdateItemContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import br.com.omnirent.item.dto.ItemUpdatedDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ItemMapper {
	
	private AddressMapper addressMapper;
	
	private CategoryMapper categoryMapper;
	
	private UserMapper userMapper;
	
	private MessageService messageService;
	
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

		ItemCreatedDTO newItem = new ItemCreatedDTO(
		        item.getId(), item.getName(), itemData.getBrand(),
		        itemData.getModel(), itemData.getDescription(), itemData.getBasePrice(),
		        itemData.getItemCondition(), item.getItemStatus());
		
		newItem.setItemConditionLabel(messageService.get(newItem.getItemCondition().getMessageKey()));
		newItem.setItemStatusLabel(messageService.get(newItem.getItemStatus().getMessageKey()));
		
		return newItem;
	}
	
	public ItemSnapshotDTO toSnapshotDTO(ItemSnapshot itemSnapshot) {
		ItemData itemData = itemSnapshot.getItemData();

		return new ItemSnapshotDTO(
			    itemSnapshot.getId(), itemSnapshot.getName(), itemData.getBrand(),
			    itemData.getModel(), itemData.getBasePrice(), itemData.getItemCondition(),
			    itemData.getDescription()
			);
	}

	public Item fromDto(ItemRequestDTO itemDTO, String ownerId, String pickupAddressId,
			String subCategoryId, ItemStatus itemStatus) {
		Item item = new Item();
		
		item.setName(itemDTO.name());
		ItemData itemData = new ItemData(itemDTO.brand(), itemDTO.model(),
				itemDTO.description(), itemDTO.basePrice(), itemDTO.itemCondition());
		
		item.setOwnerId(ownerId);
		item.setPickupAddressId(pickupAddressId);
		item.setSubCategoryId(subCategoryId);
		
		item.setItemStatus(itemStatus);
		
		item.setItemData(itemData);
		
		return item;
	}
	
	public ItemSnapshot fromRentContext(ItemInfo itemInfo, Rental rental) {
	    ItemSnapshot itemSnapshot = new ItemSnapshot(
	        itemInfo.getItemName(), itemInfo.getBrand(), itemInfo.getModel(),
	        itemInfo.getDescription(), itemInfo.getBasePrice(), itemInfo.getItemCondition()
	    );

	    itemSnapshot.setRental(rental);

	    return itemSnapshot;
	}
	
	public List<ItemDisplayDTO> localize(List<ItemDisplayDTO> displayDTOs) {
		displayDTOs.forEach(d -> {
			d.setItemConditionLabel(messageService.get(d.getItemCondition().getMessageKey()));;
			d.setItemStatusLabel(messageService.get(d.getItemStatus().getMessageKey()));
		});
		
		return displayDTOs;
	}
	
	public ItemDetailDTO localize(ItemDetailDTO itemDTO) {
		itemDTO.setItemStatusLabel(messageService.get(itemDTO.getItemStatus().getMessageKey()));
		itemDTO.setItemConditionLabel(messageService.get(itemDTO.getItemCondition().getMessageKey()));
		categoryMapper.localize(itemDTO.getSubCategory());
		
		return itemDTO;
	}
	
	public ItemEnums getLocalizedEnums() {
		List<EnumOption> itemConditions = Arrays.stream(ItemCondition.values())
				.map(i -> new EnumOption(i.name(), messageService.get(i.getMessageKey())))
				.collect(Collectors.toList());
		
		List<EnumOption> itemStatus = Arrays.stream(ItemStatus.values())
				.map(i -> new EnumOption(i.name(), messageService.get(i.getMessageKey())))
				.collect(Collectors.toList());
		
		return new ItemEnums(itemConditions, itemStatus);
	}
	
	public ItemUpdatedDTO toItemUpdatedDTO(
	        UpdateItemContext context, UpdateItemRequestDTO request) {
		ItemUpdatedDTO updatedItem = new ItemUpdatedDTO(
	            context.itemInfo().getId(), request.name(),
	            request.brand(), request.model(),
	            request.description(), request.basePrice(),
	            request.itemCondition(), context.status(),
	            context.ownerId(), context.addressId(), context.subCategoryId());
		
		updatedItem.setItemConditionLabel(messageService.get(updatedItem.getItemCondition().getMessageKey()));
		updatedItem.setItemStatusLabel(messageService.get(updatedItem.getItemStatus().getMessageKey()));
		
	    return updatedItem;
	}
	
	public ItemAuditSnapshot toAuditSnapshot(Item item) {
		ItemData itemData = item.getItemData();
	    return new ItemAuditSnapshot(
	            item.getId(), item.getName(),
	            itemData.getBrand(), itemData.getModel(),
	            itemData.getDescription(), itemData.getBasePrice(),
	            itemData.getItemCondition(), item.getItemStatus(),
	            item.getOwnerId(), item.getSubCategoryId(),
	            item.getPickupAddressId()
	    );
	}
	
	public ItemAuditSnapshot toAuditSnapshot(ItemUpdatedDTO item) {
	    return new ItemAuditSnapshot(
	            item.getId(), item.getName(),
	            item.getBrand(), item.getModel(),
	            item.getDescription(), item.getBasePrice(),
	            item.getItemCondition(), item.getItemStatus(),
	            item.getOwnerId(), item.getSubCategoryId(),
	            item.getAddressId()
	    );
	}

	public ItemAuditSnapshot toAuditSnapshot(ItemSnapshot itemSnapshot) {
	    ItemData itemData = itemSnapshot.getItemData();

	    return new ItemAuditSnapshot(
	            itemSnapshot.getId(),
	            itemSnapshot.getName(),
	            itemData.getBrand(),
	            itemData.getModel(),
	            itemData.getDescription(),
	            itemData.getBasePrice(),
	            itemData.getItemCondition(),
	            null, null, null, null);
	}
} 
