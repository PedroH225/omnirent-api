package br.com.omnirent.item;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.address.AddressService;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.user.User;
import br.com.omnirent.user.UserService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ItemService {

	private ItemRepository itemRepository;
	
	private UserService userService;
	
	private AddressService addressService;
	
	private CategoryService categoryService;
	
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

	public ItemResponseDTO addItem(ItemRequestDTO itemDTO, String userId) {
		Item item = ItemMapper.fromDto(itemDTO);
		
		item.setOwner(userService.findById(userId));
		item.setPickupAdress(addressService.findById(itemDTO.addressId()));
		item.setSubCategory(categoryService.findSubById(itemDTO.subCategoryId()));
		
		return ItemMapper.toDto(itemRepository.save(item));
	}
	
}
