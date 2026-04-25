package br.com.omnirent.item;

import java.util.List;
import java.util.Optional;

import org.springframework.core.NativeDetector.Context;
import org.springframework.stereotype.Service;

import br.com.omnirent.address.AddressService;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.exception.common.ConflictException;
import br.com.omnirent.exception.domain.ItemNotFoundException;
import br.com.omnirent.item.context.ChangeItemAddressContext;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.context.UpdateItemContext;
import br.com.omnirent.item.context.UpdateItemStatusContext;
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
	
	private ItemQueryRepository queryRepository;
	
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
		Optional<ItemDetailDTO> itemDetail = queryRepository.findItemDetailDTO(id);
		
		if (itemDetail.isEmpty()) {
			throw new ItemNotFoundException();
		}
		
		return itemDetail.get();
	}
	
	public ItemRentedContext getItemRentedContext(String id) {
		Optional<ItemRentedContext> itemOpt = queryRepository.getItemRentedContext(id);
		if (itemOpt.isEmpty()) {
			throw new ItemNotFoundException();
		}
		
		return itemOpt.get();
	}
	
	private UpdateItemContext getUpdateContext(String id) {
		Optional<UpdateItemContext> itemOpt = queryRepository.getUpdateContext(id);
		if (itemOpt.isEmpty()) {
			throw new ItemNotFoundException();
		}
		
		return itemOpt.get();
	}
	
	private UpdateItemStatusContext getUpdateStatusContext(String id) {
		Optional<UpdateItemStatusContext> itemOpt =
				queryRepository.getUpdateStatusContext(id);
		if (itemOpt.isEmpty()) {
			throw new ItemNotFoundException();
		}
		
		return itemOpt.get();
	}
	
	private ChangeItemAddressContext getChangeItemAddressContext(String id) {
		return queryRepository.getChangeAddressContext(id)
				.orElseThrow(ItemNotFoundException::new);
	}

	public List<ItemDisplayDTO> getUserItems() {
		String userId = currentUserProvider.currentUserId();
		userService.requireExistence(userId);
		return queryRepository.findUserItems(userId);
	}

	public ItemCreatedDTO addItem(ItemRequestDTO itemDTO) {
		String currentUserId = currentUserProvider.currentUserId();
		
		User user = userService.getValidReference(currentUserId);
		Address pickupAddress = addressService.getValidReference(itemDTO.addressId(), currentUserId);
		SubCategory subCategory = categoryService.getValidSubReference(itemDTO.subCategoryId());
		
		Item item = itemMapper.fromDto(itemDTO, user.getId(), pickupAddress.getId(),
				subCategory.getId(), ItemStatus.AVAILABLE);
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
	public void changePickupAddress(String itemId, String newAddressId) {
	    String currentUserId = currentUserProvider.currentUserId();
	    ChangeItemAddressContext context = getChangeItemAddressContext(itemId);

	    authorizationService.requireNotBlocked(context.status());
	    authorizationService.requireOwner(context.ownerId(), currentUserId);
	    
	    if (context.currentAddressId().equals(newAddressId)) {
	        return;
	    }
	    
	    String validatedNewAddressId = addressService
	        .getValidReference(newAddressId, currentUserId).getId();
	    
	    int updated = itemRepository.updatePickupAddress(
	        itemId, validatedNewAddressId, context.currentAddressId(), context.status());

	    if (updated == 0) {
	        throw new ConflictException("Item was modified before update.");
	    }
	}

	@Transactional
	public void updateStatus(String itemId) {
		String currentUserId = currentUserProvider.currentUserId();
		UpdateItemStatusContext context = getUpdateStatusContext(itemId);
		ItemStatus currentStatus = context.currentStatus();
		
		authorizationService.requireNotBlocked(currentStatus);
		authorizationService.requireOwner(context.ownerId(), currentUserId);
		
		ItemStatus newStatus = 
				currentStatus == ItemStatus.AVAILABLE ?
				ItemStatus.UNAVAILABLE : ItemStatus.AVAILABLE;
		
		int updated = itemRepository.updateStatus(itemId, currentStatus, newStatus);
		
		if (updated == 0) {
			throw new ConflictException("Item was modified before update.");
		}
	}
	
}
