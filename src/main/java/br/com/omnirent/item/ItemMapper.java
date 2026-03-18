package br.com.omnirent.item;

import java.util.List;
import java.util.stream.Collectors;

import br.com.omnirent.address.Address;
import br.com.omnirent.category.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;

public class ItemMapper {

	public static List<ItemResponseDTO> toDto(List<Item> item) {
		return item.stream()
				.map(ItemResponseDTO::new)
				.collect(Collectors.toList());
	}
	
	public static ItemResponseDTO toDto(Item item) {
		return new ItemResponseDTO(item);
	}

	public static Item fromDto(ItemRequestDTO itemDTO) {
		Item item = new Item();
		
		item.setName(itemDTO.name());
		item.setModel(itemDTO.model());
		item.setBrand(itemDTO.brand());
		item.setDescription(itemDTO.description());
		item.setBasePrice(itemDTO.basePrice());
		item.setItemCondition(ItemCondition.fromString(itemDTO.itemCondition()));
		item.setItemStatus(ItemStatus.fromString(itemDTO.itemStatus()));
		
		return item;
	}
	
	public static void updateItem(ItemRequestDTO itemDTO, Address address, SubCategory subCategory, Item item) {		
		item.setName(itemDTO.name());
		item.setModel(itemDTO.model());
		item.setBrand(itemDTO.brand());
		item.setDescription(itemDTO.description());
		item.setBasePrice(itemDTO.basePrice());
		item.setItemCondition(ItemCondition.fromString(itemDTO.itemCondition()));
		item.setItemStatus(ItemStatus.fromString(itemDTO.itemStatus()));
		
		if (address != null) {
			item.setPickupAdress(address);
		}
		
		if (subCategory != null) {
			item.setSubCategory(subCategory);
		}
	}
	
}
