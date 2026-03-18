package br.com.omnirent.item;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.user.User;
import br.com.omnirent.user.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ItemService {

	private ItemRepository itemRepository;
	
	private UserService userService;
	
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

	public List<ItemResponseDTO> getUserItems(String userId) {
		User user = userService.findById(userId);
		
		return ItemMapper.toDto(user.getItems());
	}
	
}
