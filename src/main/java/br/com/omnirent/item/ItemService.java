package br.com.omnirent.item;

import java.time.Clock;
import java.time.Instant;
import java.util.EnumSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.omnirent.address.AddressService;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.enums.ItemEnums;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.common.page.PageResponseDTO;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.ConcurrencyErrorType;
import br.com.omnirent.exception.domain.apptype.ItemErrorType;
import br.com.omnirent.item.context.ChangeItemAddressContext;
import br.com.omnirent.item.context.ChangeItemSubCategoryContext;
import br.com.omnirent.item.context.ItemFeedContext;
import br.com.omnirent.item.context.ItemFeedFilter;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.context.UpdateItemContext;
import br.com.omnirent.item.context.UpdateItemStatusContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.item.dto.ItemFeedDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.ItemUpdatedDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.item.event.ItemAddressChangedEvent;
import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.item.event.ItemSubcategoryChangedEvent;
import br.com.omnirent.item.event.ItemUpdatedEvent;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Service
@Slf4j
public class ItemService {

	private ItemRepository itemRepository;
	
	private ItemQueryRepository queryRepository;
	
	private UserService userService;
	
	private CurrentUserProvider currentUserProvider;
	
	private AddressService addressService;
	
	private CategoryService categoryService;
	
	private ItemAuthorizationService authorizationService;
	
	private ItemMapper itemMapper;

	private ItemImageRepository imageRepository;
	
	private SpringDomainEventPublisher eventPublisher;
	
	private MessageService messageService;
	
	private Clock clock;
		
	public ItemDetailDTO getItemById(String id) {
		ItemDetailDTO result = queryRepository.findItemDetailDTO(id)
				.orElseThrow(() -> new ApiException(ItemErrorType.NOT_FOUND));
		
		result.setImages(imageRepository.findItemImages(id));
		
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
	
	public PageResponseDTO<ItemFeedDTO> getItemFeed(ItemFeedFilter feedFilter, Pageable pageable) {
		Page<ItemFeedContext> context = queryRepository.getFeedContexts(
				feedFilter.itemName(), feedFilter.categoryName(), 
				feedFilter.subCategoryName(), feedFilter.itemCondition(),
				pageable);
		
		return itemMapper.toFeedDtos(context);
	}

	@Transactional
	public ItemCreatedDTO addItem(ItemRequestDTO itemDTO) {
		String currentUserId = currentUserProvider.currentUserId();
				
		User user = userService.getValidReference(currentUserId);
		Address pickupAddress = addressService.getValidReference(itemDTO.addressId(), currentUserId);
		SubCategory subCategory = categoryService.getValidSubReference(itemDTO.subCategoryId());
		
		Item item = itemMapper.fromDto(itemDTO, user.getId(), pickupAddress.getId(),
				subCategory.getId(), ItemStatus.ANALISYS);
		
		Item persistedItem = itemRepository.save(item);
		
		eventPublisher.publish(new ItemCreatedEvent(
				AuditAction.ITEM_CREATED, currentUserId, item.getId(), 
				itemMapper.toAuditSnapshot(persistedItem), Instant.now(clock)));
		
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
				AuditAction.ITEM_UPDATED, currentUserId, itemUpdatedDTO.getId(), 
				itemMapper.toAuditSnapshot(itemUpdatedDTO),
				itemMapper.buildPreviousSnapshot(context),
				Instant.now(clock)));
		
		return itemUpdatedDTO;
	}
	
	@Transactional
	public void changePickupAddress(String itemId, String newAddressId) {
	    String currentUserId = currentUserProvider.currentUserId();
	    ChangeItemAddressContext context = getChangeItemAddressContext(itemId);

	    authorizationService.requireNotBlocked(context.status());
	    authorizationService.requireOwner(context.ownerId(), currentUserId);
	    
	    String previousAddressId = context.currentAddressId();
	    if (previousAddressId.equals(newAddressId)) {
	        return;
	    }
	    
	    String validatedNewAddressId = addressService
	        .getValidReference(newAddressId, currentUserId).getId();
	    
	    int updated = itemRepository.updatePickupAddress(
	        itemId, validatedNewAddressId, previousAddressId, context.status());

	    if (updated == 0) {
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
	    }
	    
	    eventPublisher.publish(new ItemAddressChangedEvent(
	    		AuditAction.ITEM_ADDRESS_CHANGED, currentUserId, context.id(), 
				itemMapper.toReassignedAuditSnapshot(validatedNewAddressId),
				itemMapper.toReassignedAuditSnapshot(previousAddressId),
				Instant.now(clock)));
	}
	
	@Transactional
	public void changeSubCategory(String itemId, String newSubCategory) {
	    String currentUserId = currentUserProvider.currentUserId();
	    ChangeItemSubCategoryContext context = getChangeItemSubCategoryContext(itemId);

	    authorizationService.requireNotBlocked(context.status());
	    authorizationService.requireOwner(context.ownerId(), currentUserId);
	    
	    String previousSubCategoryId = context.currentSubCategoryId();
	    if (previousSubCategoryId.equals(newSubCategory)) {
	        return;
	    }
	    
	    String validatedNewSubCatId = categoryService
	        .getValidSubReference(newSubCategory).getId();
	    
	    int updated = itemRepository.updateItemSubCategory(
	        itemId, validatedNewSubCatId, previousSubCategoryId, context.status());

	    if (updated == 0) {
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
	    }
	    
	    eventPublisher.publish(new ItemSubcategoryChangedEvent(
	    		AuditAction.ITEM_CATEGORY_CHANGED, currentUserId, context.id(), 
				itemMapper.toReassignedAuditSnapshot(validatedNewSubCatId),
				itemMapper.toReassignedAuditSnapshot(previousSubCategoryId),
				Instant.now(clock)));
	}
	
	@Transactional
	public void changeAvailability(String itemId) {
		EnumSet<ItemStatus> required = EnumSet.of(
				ItemStatus.UNAVAILABLE, ItemStatus.AVAILABLE);
		
		String currUserId = currentUserProvider.currentUserId();
		UpdateItemStatusContext context = getUpdateStatusContext(itemId);
		ItemStatus currStatus = context.currentStatus();

		if (!required.contains(currStatus)) {
			throw new ApiException(ItemErrorType.CHANGE_AVAILABILITY_ERROR);
		}
		
		ItemStatus targetStatus = currStatus == ItemStatus.AVAILABLE ?
				ItemStatus.UNAVAILABLE : ItemStatus.AVAILABLE;
		
		authorizationService.validateItemFromDB(itemId, currUserId);
		
		updateStatus(itemId, currStatus, targetStatus);
	}
	
	@Transactional
	public void markRentedItem(String itemId) {
		UpdateItemStatusContext context = getUpdateStatusContext(itemId);
		ItemStatus currStatus = context.currentStatus();
		ItemStatus targetStatus = ItemStatus.RENTED;
		
		authorizationService.requireNotBlocked(currStatus);
		validateTransition(currStatus, targetStatus);
		
		updateStatus(itemId, currStatus, targetStatus);
	}
	
	@Transactional
	public void recalculateAvailability(String itemId, RentalStatus rentalStatus) {
		EnumSet<RentalStatus> cancelledRentalContext = 
				EnumSet.of(RentalStatus.CANCELLED, RentalStatus.EXPIRED);
		
		UpdateItemStatusContext context = getUpdateStatusContext(itemId);
		ItemStatus currStatus = context.currentStatus();
		ItemStatus targetStatus = ItemStatus.UNAVAILABLE;
		
		if (context.ownerStatus() == UserStatus.BANNED) {
			targetStatus = ItemStatus.BLOCKED;
		} 
		else if (cancelledRentalContext.contains(rentalStatus)) {
			targetStatus = ItemStatus.AVAILABLE;
		}		
		
		if (currStatus != targetStatus) {
		    updateStatus(itemId, currStatus, targetStatus);
		}
	}
	
	@Transactional
	public void aproveItem(String itemId) {
		UpdateItemStatusContext context = getUpdateStatusContext(itemId);
		ItemStatus currStatus = context.currentStatus();
		ItemStatus targetStatus = ItemStatus.AVAILABLE;
		
		if (currStatus != ItemStatus.ANALISYS) {
			throw new ApiException(ItemErrorType.ANALYSIS_REQUIRED);
		}
				
		updateStatus(itemId, currStatus, targetStatus);		
	}	
	
	@Transactional
	public void rejectItem(String itemId) {
		UpdateItemStatusContext context = getUpdateStatusContext(itemId);
		ItemStatus currStatus = context.currentStatus();
		ItemStatus targetStatus = ItemStatus.BLOCKED;
		
		if (currStatus != ItemStatus.ANALISYS) {
			throw new ApiException(ItemErrorType.ANALYSIS_REQUIRED);
		}
				
		updateStatus(itemId, currStatus, targetStatus);		
	}	
	
	private void updateStatus(String itemId, ItemStatus currStatus, ItemStatus targetStatus) {
		int updated = itemRepository.updateStatus(itemId, currStatus, targetStatus);
		
		if (updated == 0) {
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
		}
	}
		
	private void validateTransition(ItemStatus currStatus, ItemStatus targetStatus) {
		if (!currStatus.canTransition(targetStatus)) {
			throw new ApiException(ItemErrorType.INVALID_STATUS_TRANSITION,
					messageService.get(currStatus.getMessageKey()),
					messageService.get(targetStatus.getMessageKey()));
		}
	}
	
	public ItemEnums getEnums() {
		return itemMapper.getLocalizedEnums();
	}
}
