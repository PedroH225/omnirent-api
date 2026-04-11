package br.com.omnirent.item;

import java.util.List;
import java.util.stream.Collectors;

import br.com.omnirent.address.Address;
import br.com.omnirent.category.SubCategory;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.domain.ItemRequestDTO;
import br.com.omnirent.item.domain.ItemResponseDTO;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.domain.User;

public class ItemMapper {

	public static List<ItemResponseDTO> toDto(List<Item> item) {
		return item.stream()
				.map(ItemResponseDTO::new)
				.collect(Collectors.toList());
	}
	
	public static ItemResponseDTO toDto(Item item) {
		return new ItemResponseDTO(item);
	}

	public static Item fromDto(ItemRequestDTO itemDTO, User owner, Address pickUpAddress,
			SubCategory subCategory, ItemStatus itemStatus) {
		Item item = new Item();
		
		item.setName(itemDTO.name());
		ItemData itemData = new ItemData(itemDTO);
		
		item.assignOwner(owner);
		item.assignAddress(pickUpAddress);
		item.assignSubCategory(subCategory);
		
		item.setItemStatus(itemStatus);
		
		item.setItemData(itemData);
		
		return item;
	}
	
	public static void updateItem(ItemRequestDTO itemDTO, Address address, SubCategory subCategory, Item item) {		
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
	
	public static ItemSnapshot fromItem(Item item, Rental rental) {
		ItemSnapshot itemSnapshot = new ItemSnapshot(item);
		
		itemSnapshot.setRental(rental);
		
		return itemSnapshot;
	}
	
}
