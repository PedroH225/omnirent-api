package br.com.omnirent.item;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import br.com.omnirent.address.AddressService;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.exception.common.ConflictException;
import br.com.omnirent.exception.domain.ItemNotFoundException;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.context.UpdateItemContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ItemService {

	private ItemRepository itemRepository;
	
	private UserService userService;
	
	private CurrentUserProvider currentUserProvider;
	
	private AddressService addressService;
	
	private CategoryService categoryService;
	
	private ItemAuthorizationService authorizationService;
	
	private ItemMapper itemMapper;
	
	public Item findById(String id) {
		Optional<Item> item = itemRepository.findById(id);
		
		if (item.isEmpty()) {
			throw new ItemNotFoundException();
		}
		
		return item.get();
	}
	
	public ItemDetailDTO getItemById(String id) {
		Optional<ItemDetailDTO> itemDetail = itemRepository.findItemDetailDTO(id);
		
		if (itemDetail.isEmpty()) {
			throw new ItemNotFoundException();
		}
		
		return itemDetail.get();
	}
	
	public ItemRentedContext getItemRentedContext(String id) {
		Optional<ItemRentedContext> itemOpt = itemRepository.getItemRentedContext(id);
		if (itemOpt.isEmpty()) {
			throw new ItemNotFoundException();
		}
		
		return itemOpt.get();
	}
	
	private UpdateItemContext getUpdateContext(String id) {
		Optional<UpdateItemContext> itemOpt = itemRepository.getUpdateContext(id);
		if (itemOpt.isEmpty()) {
			throw new ItemNotFoundException();
		}
		
		return itemOpt.get();
	}

	public List<ItemDisplayDTO> getUserItems() {
		String userId = currentUserProvider.currentUserId();
		userService.requireExistence(userId);
		return itemRepository.findUserItems(userId);
	}

	public ItemCreatedDTO addItem(ItemRequestDTO itemDTO) {
		String userId = currentUserProvider.currentUserId();
		userService.requireExistence(userId);
		User user = userService.getUserReference(userId);
		
		Address pickupAddress = addressService.findById(itemDTO.addressId());
		SubCategory subCategory = categoryService.findSubById(itemDTO.subCategoryId());
		
		Item item = itemMapper.fromDto(itemDTO, user, userId, 
				pickupAddress, subCategory,
				ItemStatus.AVAILABLE);
		return itemMapper.toCreatedDto(itemRepository.save(item));
	}
	
	@Transactional
	public void updateItem(UpdateItemRequestDTO request) {
		String currentUserId = currentUserProvider.currentUserId();
		UpdateItemContext context = getUpdateContext(request.id());
		
		authorizationService.requireOwner(context.getOwnerId(), currentUserId);
		
		int updated = itemRepository.updateItem(
			context.getItemInfo().getId(), request.name(), request.brand(),
			request.model(), request.description(), request.basePrice(),
			ItemCondition.fromString(request.itemCondition())
		);
		
		if (updated == 0) {
			throw new ConflictException("Unexpected error updating item.");
		}
	}

	@Transactional
	public ItemDetailDTO updateStatus(String itemId, String itemStatusStr) {
		String currentUserId = currentUserProvider.currentUserId();
		Item item = findById(itemId);
		
		authorizationService.requireOwner(item, currentUserId);
		
		item.updateItemStatus(itemStatusStr);
		
		return itemMapper.toDto(itemRepository.save(item));
	}
	
}
