package br.com.omnirent.item;

import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

	public static List<ItemResponseDTO> toDto(List<Item> item) {
		return item.stream()
				.map(ItemResponseDTO::new)
				.collect(Collectors.toList());
	}
	
	public static ItemResponseDTO toDto(Item item) {
		return new ItemResponseDTO(item);
	}
}
