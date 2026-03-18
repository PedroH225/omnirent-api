package br.com.omnirent.item;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ItemService {

	private ItemRepository itemRepository;
	
	public Item findById(String id) {
		Optional<Item> item = itemRepository.findById(id);
		
		if (item.isEmpty()) {
			throw new RuntimeException("Item not found.");
		}
		
		return item.get();
	}
	
	public ItemResponseDTO getItemById(String id) {
		return ItemMapper.toDto(findById(id));
	}
	
}
