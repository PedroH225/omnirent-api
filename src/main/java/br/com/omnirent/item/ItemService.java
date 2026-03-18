package br.com.omnirent.item;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import br.com.omnirent.address.Address;
import br.com.omnirent.address.AddressService;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.SubCategory;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.user.User;
import br.com.omnirent.user.UserService;
import jakarta.transaction.Transactional;
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
	
	@Transactional
	public ItemResponseDTO updateItem(ItemRequestDTO itemDTO, String userId) {
		User user = userService.findById(userId);
		Item updatedItem = findById(itemDTO.id());
				
		Address address = null;
		if (StringUtils.isNotBlank(itemDTO.addressId()) && 
				!updatedItem.getPickupAdress().getId().equals(itemDTO.addressId())) {
			address = addressService.findById(itemDTO.addressId());
		}
		
		SubCategory subCategory = null;
		if (StringUtils.isNotBlank(itemDTO.subCategoryId()) && 
				!updatedItem.getSubCategory().getId().equals(itemDTO.subCategoryId())) {
			subCategory = categoryService.findSubById(itemDTO.subCategoryId());
		}
		
		ItemMapper.updateItem(itemDTO, address, subCategory, updatedItem);
		
		return ItemMapper.toDto(itemRepository.save(updatedItem));

	}

	@Transactional
	public ItemResponseDTO updateStatus(String itemId, String itemStatusStr) {
		Item item = findById(itemId);
		
		item.updateItemStatus(itemStatusStr);
		
		return ItemMapper.toDto(itemRepository.save(item));
	}
	
}
