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
import br.com.omnirent.exception.domain.ItemNotFoundException;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemRequestDTO;
import br.com.omnirent.item.domain.ItemResponseDTO;
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
	
	private ItemAuthorizationService authorizationService;
	
	public Item findById(String id) {
		Optional<Item> item = itemRepository.findById(id);
		
		if (item.isEmpty()) {
			throw new ItemNotFoundException();
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
		User user = userService.findById(userId);
		Address pickupAddress = addressService.findById(itemDTO.addressId());
		SubCategory subCategory = categoryService.findSubById(itemDTO.subCategoryId());
		
		Item item = ItemMapper.fromDto(itemDTO, user, 
				pickupAddress, subCategory,
				ItemStatus.AVAILABLE);
		
		return ItemMapper.toDto(itemRepository.save(item));
	}
	
	@Transactional
	public ItemResponseDTO updateItem(ItemRequestDTO itemDTO, String currentUserId) {
		Item updatedItem = findById(itemDTO.id());
		
		authorizationService.requireOwner(updatedItem, currentUserId);

		Address address = null;
		if (StringUtils.isNotBlank(itemDTO.addressId()) && 
				!updatedItem.getPickupAddressId().equals(itemDTO.addressId())) {
			address = addressService.findById(itemDTO.addressId());
		}
		
		SubCategory subCategory = null;
		if (StringUtils.isNotBlank(itemDTO.subCategoryId()) && 
				!updatedItem.getSubCategoryId().equals(itemDTO.subCategoryId())) {
			subCategory = categoryService.findSubById(itemDTO.subCategoryId());
		}
		
		ItemMapper.updateItem(itemDTO, address, subCategory, updatedItem);
		
		return ItemMapper.toDto(itemRepository.save(updatedItem));
	}

	@Transactional
	public ItemResponseDTO updateStatus(String itemId, String itemStatusStr, String currentUserId) {
		Item item = findById(itemId);
		
		authorizationService.requireOwner(item, currentUserId);
		
		item.updateItemStatus(itemStatusStr);
		
		return ItemMapper.toDto(itemRepository.save(item));
	}
	
}
