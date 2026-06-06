package br.com.omnirent.item;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.omnirent.address.AddressService;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemEnums;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.ConcurrencyErrorType;
import br.com.omnirent.exception.domain.ItemErrorType;
import br.com.omnirent.item.context.ChangeItemAddressContext;
import br.com.omnirent.item.context.ChangeItemSubCategoryContext;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.context.UpdateItemContext;
import br.com.omnirent.item.context.UpdateItemStatusContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.ItemUpdatedDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.item.event.ItemAddressChangedEvent;
import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.item.event.ItemStatusUpdatedEvent;
import br.com.omnirent.item.event.ItemSubcategoryChangedEvent;
import br.com.omnirent.item.event.ItemUpdatedEvent;
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
	
	private SpringDomainEventPublisher eventPublisher;
		
	public ItemDetailDTO getItemById(String id) {
		ItemDetailDTO result = queryRepository.findItemDetailDTO(id)
				.orElseThrow(() -> new ApiException(ItemErrorType.NOT_FOUND));
		
		return itemMapper.localize(result);	
	}
	
	public ItemRentedContext getItemRentedContext(String id) {
		return queryRepository.getItemRentedContext(id)
				.orElseThrow(() -> new ApiException(ItemErrorType.NOT_FOUND));
	}
	
	private UpdateItemContext getUpdateContext(String id) {
		return queryRepository.getUpdateContext(id)
				.orElseThrow(() -> new ApiException(ItemErrorType.NOT_FOUND));
	}
	
	private UpdateItemStatusContext getUpdateStatusContext(String id) {
		return queryRepository.getUpdateStatusContext(id)
				.orElseThrow(() -> new ApiException(ItemErrorType.NOT_FOUND));
	}
	
	private ChangeItemAddressContext getChangeItemAddressContext(String id) {
		return queryRepository.getChangeAddressContext(id)
				.orElseThrow(() -> new ApiException(ItemErrorType.NOT_FOUND));
	}

	private ChangeItemSubCategoryContext getChangeItemSubCategoryContext(String id) {
		return queryRepository.getChangeSubCategoryContext(id)
				.orElseThrow(() -> new ApiException(ItemErrorType.NOT_FOUND));
	}
	
	public List<ItemDisplayDTO> getUserItems() {
		String userId = currentUserProvider.currentUserId();
		userService.requireExistence(userId);
		List<ItemDisplayDTO> result = queryRepository.findUserItems(userId);
		
		return itemMapper.localize(result);
	}

	public ItemCreatedDTO addItem(ItemRequestDTO itemDTO) {
		String currentUserId = currentUserProvider.currentUserId();
				
		User user = userService.getValidReference(currentUserId);
		Address pickupAddress = addressService.getValidReference(itemDTO.addressId(), currentUserId);
		SubCategory subCategory = categoryService.getValidSubReference(itemDTO.subCategoryId());
		
		Item item = itemMapper.fromDto(itemDTO, user.getId(), pickupAddress.getId(),
				subCategory.getId(), ItemStatus.AVAILABLE);
		
		Item persistedItem = itemRepository.save(item);
		
		eventPublisher.publish(new ItemCreatedEvent(
				currentUserId, item.getId(), 
				itemMapper.toAuditSnapshot(persistedItem), Instant.now()));
		
		return itemMapper.toCreatedDto(persistedItem);
	}
	
	@Transactional
	public ItemUpdatedDTO updateItem(UpdateItemRequestDTO request) {
		String currentUserId = currentUserProvider.currentUserId();
		
		UpdateItemContext context = getUpdateContext(request.id());
		
	    authorizationService.requireNotBlocked(context.status());
		authorizationService.requireOwner(context.ownerId(), currentUserId);
		
		int updated = itemRepository.updateItem(
			context.itemInfo().getId(), context.status(), request.name(), request.brand(),
			request.model(), request.description(), request.basePrice(),
			request.itemCondition()
		);
		
		if (updated == 0) {
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
		}
		
		ItemUpdatedDTO itemUpdatedDTO = itemMapper.toItemUpdatedDTO(context, request);
		
		eventPublisher.publish(new ItemUpdatedEvent(
				currentUserId, itemUpdatedDTO.getId(), 
				itemMapper.toAuditSnapshot(itemUpdatedDTO), Instant.now()));
		
		return itemUpdatedDTO;
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
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
	    }
	    
	    eventPublisher.publish(new ItemAddressChangedEvent(
				currentUserId, context.id(), 
				validatedNewAddressId, Instant.now()));
	}
	
	@Transactional
	public void changeSubCategory(String itemId, String newSubCategory) {
	    String currentUserId = currentUserProvider.currentUserId();
	    ChangeItemSubCategoryContext context = getChangeItemSubCategoryContext(itemId);

	    authorizationService.requireNotBlocked(context.status());
	    authorizationService.requireOwner(context.ownerId(), currentUserId);
	    
	    if (context.currentSubCategoryId().equals(newSubCategory)) {
	        return;
	    }
	    
	    String validatedNewSubCatId = categoryService
	        .getValidSubReference(newSubCategory).getId();
	    
	    int updated = itemRepository.updateItemSubCategory(
	        itemId, validatedNewSubCatId, context.currentSubCategoryId(), context.status());

	    if (updated == 0) {
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
	    }
	    
	    eventPublisher.publish(new ItemSubcategoryChangedEvent(
				currentUserId, context.id(), 
				validatedNewSubCatId, Instant.now()));
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
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
		}
		
		eventPublisher.publish(new ItemStatusUpdatedEvent(
				currentUserId, context.id(), 
				newStatus, Instant.now()));
	}

	public ItemEnums getEnums() {
		return itemMapper.getLocalizedEnums();
	}
	
}
