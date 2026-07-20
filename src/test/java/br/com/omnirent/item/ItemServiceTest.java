package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.address.AddressService;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemRejectionReason;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.config.i18n.MessageService;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AddressErrorType;
import br.com.omnirent.exception.domain.apptype.ItemErrorType;
import br.com.omnirent.exception.domain.apptype.SubCategoryErrorType;
import br.com.omnirent.exception.domain.apptype.UserErrorType;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.item.context.ChangeItemAddressContext;
import br.com.omnirent.item.context.ChangeItemSubCategoryContext;
import br.com.omnirent.item.context.ItemRejectedRequestDto;
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
import br.com.omnirent.item.event.ItemApprovedEvent;
import br.com.omnirent.item.event.ItemRejectedEvent;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

	@InjectMocks
	private ItemService itemService;
	
	@Mock
	private ItemRepository itemRepository;
	
	@Mock
	private ItemQueryRepository queryRepository;

	@Mock
	private UserService userService;

	@Mock
	private CurrentUserProvider currentUserProvider;

	@Mock
	private AddressService addressService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private ItemAuthorizationService authorizationService;

	@Mock
	private ItemMapper itemMapper;
	
	@Mock
	private SpringDomainEventPublisher eventPublisher;
	
	@Mock
	private Clock clock;
	
	@Mock
	private ItemImageRepository imageRepository;
	
	@Mock
	private MessageService messageService;
	
	private User owner;
	private User owner2;

	private Address ownerAddress;
	private Address ownerAddress2;
	
	private Address owner2Address;

	private Category tools;
	private SubCategory drill;
	private SubCategory hammer;

	private Item item;
	private Item item2;
	
	@BeforeEach
	void setUp() {
		owner = UserTestFactory.persistedOwner();
		owner2 = UserTestFactory.persistedOwner();

		ownerAddress = AddressTestFactory.forPersistedUser(owner);
		ownerAddress2 = AddressTestFactory.forPersistedUser(owner);

		owner2Address = AddressTestFactory.forPersistedUser(owner2);

        tools = CategoryTestFactory.createPersisted("Tools");
        drill = SubCategoryTestFactory.createPersisted("Drill", tools);
        hammer = SubCategoryTestFactory.createPersisted("Hammer", tools);

        item = ItemTestFactory.createPersisted(owner, ownerAddress, drill,
        		"200", ItemCondition.NEW);
        
        item2 = ItemTestFactory.createPersisted(owner, ownerAddress, drill,
        		"100", ItemCondition.USED);
	}
	
	@Test
	void shouldGetItemDetailById() {
		String itemId = item.getId();
		ItemDetailDTO itemDetailDTO = ItemTestFactory.toItemDetailsDto(item, drill, ownerAddress, owner);
	
		when(queryRepository.findItemDetailDTO(itemId)).thenReturn(Optional.of(itemDetailDTO));
		when(itemMapper.localize(itemDetailDTO)).thenReturn(itemDetailDTO);
		
		ItemDetailDTO result = itemService.getItemById(itemId);
		
		assertThat(result).isEqualTo(itemDetailDTO);
		
		verify(queryRepository).findItemDetailDTO(itemId);
	}
	
	@Test
	void shouldThrowWhenItemNotFound() {
		String invalidId = "invalidId";
	
		when(queryRepository.findItemDetailDTO(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> itemService.getItemById(invalidId))
			.isInstanceOf(ApiException.class);
				
		verify(queryRepository).findItemDetailDTO(invalidId);
	}
	
	@Test
	void shouldGetItemRentedContextById() {
		String itemId = item.getId();
		ItemRentedContext context = ItemTestFactory.toItemRentedContext(item, ownerAddress, owner);
	
		when(queryRepository.getItemRentedContext(itemId)).thenReturn(Optional.of(context));
		
		ItemRentedContext result = itemService.getItemRentedContext(itemId);
		
		assertThat(result).isEqualTo(context);
		
		verify(queryRepository).getItemRentedContext(itemId);
	}
	
	@Test
	void shouldThrowWhenItemRentedContextNotFound() {
		String invalidId = "invalidId";
		
		when(queryRepository.getItemRentedContext(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> itemService.getItemRentedContext(invalidId))
			.isInstanceOf(ApiException.class);
				
		verify(queryRepository).getItemRentedContext(invalidId);
		verifyNoMoreInteractions(itemRepository);
	}
	
	@Test
	void shouldGetUserItems() {
		String userId = owner.getId();
		
		ItemDisplayDTO dto1 = ItemTestFactory.toItemDisplayDTO(item, drill, owner);
		ItemDisplayDTO dto2 = ItemTestFactory.toItemDisplayDTO(item2, drill, owner);
		List<ItemDisplayDTO> expected = List.of(dto1, dto2);
		
		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(queryRepository.findUserItems(userId)).thenReturn(expected);
		when(itemMapper.localize(expected)).thenReturn(expected);

		List<ItemDisplayDTO> result = itemService.getUserItems();
		
		assertThat(result).isEqualTo(expected);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(userId);
		verify(queryRepository).findUserItems(userId);
	}
	
	@Test
	void shouldThrowWhenUserNotFoundOnGetUserItems() {
		String invalidId = "invalid-id";
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);	
		doThrow(new ApiException(UserErrorType.NOT_FOUND)).when(userService).requireExistence(invalidId);
	
		assertThatThrownBy(() -> itemService.getUserItems())
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(invalidId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldAddItem() {
		String ownerId = owner.getId();
		String addressId = ownerAddress.getId();
		String categoryId = drill.getId();
		
		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", ItemCondition.NEW, categoryId, addressId);
		
		Item mappedItem = ItemTestFactory.fromNewItemRequestDTO(request, drill, ownerAddress, owner);
		Item persistedItem = ItemTestFactory.toPersisted(mappedItem);
		ItemCreatedDTO expected = ItemTestFactory.toItemCreatedDTO(persistedItem);
		
		when(currentUserProvider.currentUserId()).thenReturn(ownerId);
		when(userService.getValidReference(ownerId)).thenReturn(owner);
		when(addressService.getValidReference(addressId, ownerId)).thenReturn(ownerAddress);
		when(categoryService.getValidSubReference(categoryId)).thenReturn(drill);
		
		when(itemMapper.fromDto(request, ownerId, addressId, categoryId, ItemStatus.ANALISYS))
		.thenReturn(mappedItem);
		when(itemRepository.save(mappedItem)).thenReturn(persistedItem);
		when(itemMapper.toCreatedDto(persistedItem)).thenReturn(expected);
		
		ItemCreatedDTO result = itemService.addItem(request);
		
		assertThat(result).isEqualTo(expected);
		
	    verify(currentUserProvider).currentUserId();
		verify(itemRepository).save(mappedItem);
	}
	
	@Test
	void shouldThrowWhenUserNotFoundOnAddItem() {
		String invalidId = "invalidId";
		
		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", ItemCondition.NEW, drill.getId(), ownerAddress.getId());
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);
		doThrow(new ApiException(UserErrorType.NOT_FOUND))
		.when(userService).getValidReference(invalidId);
		
		assertThatThrownBy(() -> itemService.addItem(request))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenAddressNotFoundOnAddItem() {
		String invalidOwnerId = owner.getId();
		String invalidAddressId = "invalidId";

		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", ItemCondition.NEW, drill.getId(), invalidAddressId);
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidOwnerId);
		doThrow(new ApiException(AddressErrorType.NOT_FOUND))
		.when(addressService).getValidReference(invalidAddressId, invalidOwnerId);
		
		assertThatThrownBy(() -> itemService.addItem(request))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenSubCategoryNotFoundOnAddItem() {
		String ownerId = owner.getId();
		String invalidId = "invalidId";
		
		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", ItemCondition.NEW, invalidId, ownerAddress.getId());
		
		when(currentUserProvider.currentUserId()).thenReturn(ownerId);
		doThrow(new ApiException(SubCategoryErrorType.NOT_FOUND))
		.when(categoryService).getValidSubReference(invalidId);
		
		assertThatThrownBy(() -> itemService.addItem(request))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldUpdateItem() {
		String currentUserId = owner.getId();
		
		UpdateItemRequestDTO request = ItemTestFactory.updateItemRequest(item.getId(), "200", ItemCondition.USED);
		UpdateItemContext context = ItemTestFactory.updateItemContext(item, currentUserId);

		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getUpdateContext(item.getId())).thenReturn(Optional.of(context));
		when(itemRepository.updateItem(
			    context.itemInfo().getId(), context.status(), request.name(),
			    request.brand(), request.model(), request.description(),
			    request.basePrice(), request.itemCondition()
			)).thenReturn(1); 
		when(itemMapper.toItemUpdatedDTO(context, request)).thenReturn(mock(ItemUpdatedDTO.class));
		
		itemService.updateItem(request);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verify(itemRepository).updateItem(
			    context.itemInfo().getId(), context.status(), request.name(),
			    request.brand(), request.model(), request.description(),
			    request.basePrice(), request.itemCondition());
		verifyNoMoreInteractions(itemRepository, authorizationService, currentUserProvider);
	}
	
	@Test
	void shouldThrowWhenItemIsBlockedOnUpdateItem() {
		String currentUserId = owner.getId();
		
		UpdateItemRequestDTO request = ItemTestFactory.updateItemRequest(item.getId(), "200", ItemCondition.USED);
		UpdateItemContext context = ItemTestFactory.updateItemContext(item, currentUserId);

		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getUpdateContext(item.getId())).thenReturn(Optional.of(context));
		doThrow(new ApiException(ItemErrorType.BLOCKED)).when(authorizationService).requireNotBlocked(context.status());
		
		assertThatThrownBy(() -> itemService.updateItem(request))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(queryRepository).getUpdateContext(item.getId());
		verify(authorizationService).requireNotBlocked(context.status());
		verifyNoMoreInteractions(queryRepository, authorizationService, currentUserProvider);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenUserIsNotOwnerOnUpdateItem() {
		String currentUserId = owner2.getId();
		String actualOwner = owner.getId();
		
		UpdateItemRequestDTO request = ItemTestFactory.updateItemRequest(item.getId(), "200", ItemCondition.USED);
		UpdateItemContext context = ItemTestFactory.updateItemContext(item, actualOwner);

		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getUpdateContext(item.getId())).thenReturn(Optional.of(context));
		doThrow(new ApiException(ItemErrorType.OWNER_REQUIRED)).when(authorizationService).requireOwner(actualOwner, currentUserId);
		
		assertThatThrownBy(() -> itemService.updateItem(request))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(queryRepository).getUpdateContext(item.getId());
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(actualOwner, currentUserId);
		verifyNoMoreInteractions(queryRepository, authorizationService, currentUserProvider);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenConcurrentUpdate() {
		String currentUserId = owner.getId();
		
		UpdateItemRequestDTO request = ItemTestFactory.updateItemRequest(item.getId(), "200", ItemCondition.USED);
		UpdateItemContext context = ItemTestFactory.updateItemContext(item, currentUserId);

		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getUpdateContext(item.getId())).thenReturn(Optional.of(context));
		when(itemRepository.updateItem(
			    context.itemInfo().getId(), context.status(), request.name(),
			    request.brand(), request.model(), request.description(),
			    request.basePrice(), request.itemCondition()
			)).thenReturn(0); 
		
		assertThatThrownBy(() -> itemService.updateItem(request))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verify(itemRepository).updateItem(
			    context.itemInfo().getId(), context.status(), request.name(),
			    request.brand(), request.model(), request.description(),
			    request.basePrice(), request.itemCondition());
		verifyNoMoreInteractions(itemRepository, authorizationService, currentUserProvider);
	}
	
	@Test
	void shouldChangeItemAddress() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newAddressId = ownerAddress2.getId();	
		String validatedAddressId = ownerAddress2.getId();

		ChangeItemAddressContext context = ItemTestFactory.toChangeAddressContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeAddressContext(itemId)).thenReturn(Optional.of(context));
		when(addressService.getValidReference(newAddressId, currentUserId)).thenReturn(ownerAddress2);
		when(itemRepository.updatePickupAddress(
			itemId, validatedAddressId, context.currentAddressId(), context.status()))
		.thenReturn(1);
		
		itemService.changePickupAddress(itemId, newAddressId);

		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verify(itemRepository).updatePickupAddress(itemId, validatedAddressId, context.currentAddressId(), context.status());
	}
	
	@Test
	void shouldThrowWhenConcurrentChangeAddress() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newAddressId = ownerAddress2.getId();	
		String validatedAddressId = ownerAddress2.getId();

		ChangeItemAddressContext context = ItemTestFactory.toChangeAddressContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeAddressContext(itemId)).thenReturn(Optional.of(context));
		when(addressService.getValidReference(newAddressId, currentUserId)).thenReturn(ownerAddress2);
		when(itemRepository.updatePickupAddress(
			itemId, validatedAddressId, context.currentAddressId(), context.status()))
		.thenReturn(0);
		
		assertThatThrownBy(() -> itemService.changePickupAddress(itemId, newAddressId))
		.isInstanceOf(ApiException.class);

		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verify(itemRepository).updatePickupAddress(itemId, validatedAddressId, context.currentAddressId(), context.status());
	}
	
	@Test
	void shouldDoNothingWhenItemAddressEqualsNewAddress() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newAddressId = ownerAddress.getId();	

		ChangeItemAddressContext context = ItemTestFactory.toChangeAddressContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeAddressContext(itemId)).thenReturn(Optional.of(context));

		itemService.changePickupAddress(itemId, newAddressId);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenAddressNotFound() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newAddressId = owner2Address.getId();	

		ChangeItemAddressContext context = ItemTestFactory.toChangeAddressContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeAddressContext(itemId)).thenReturn(Optional.of(context));
		doThrow(new ApiException(AddressErrorType.NOT_FOUND))
		.when(addressService).getValidReference(newAddressId, currentUserId);
		
		assertThatThrownBy(() -> itemService.changePickupAddress(itemId, newAddressId))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenUserIsNotOwnerOnChangeAddress() {
		String currentUserId = owner2.getId();
		String itemId = item.getId();
		String newAddressId = ownerAddress2.getId();	

		ChangeItemAddressContext context = ItemTestFactory.toChangeAddressContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeAddressContext(itemId)).thenReturn(Optional.of(context));
		doThrow(new ApiException(ItemErrorType.OWNER_REQUIRED))
		.when(authorizationService).requireOwner(context.ownerId(), currentUserId);

		assertThatThrownBy(() -> itemService.changePickupAddress(itemId, newAddressId))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenItemIsBlockedOnChangeAddress() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newAddressId = ownerAddress2.getId();	

		ChangeItemAddressContext context = ItemTestFactory.toChangeAddressContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeAddressContext(itemId)).thenReturn(Optional.of(context));
		doThrow(new ApiException(ItemErrorType.BLOCKED))
		.when(authorizationService).requireNotBlocked(context.status());

		assertThatThrownBy(() -> itemService.changePickupAddress(itemId, newAddressId))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verifyNoMoreInteractions(authorizationService);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldChangeItemSubCategory() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newSubCategoryId = hammer.getId();	
		String validatedNewSubCatId = hammer.getId();

		ChangeItemSubCategoryContext context = ItemTestFactory.toChangeSubCategoryContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeSubCategoryContext(itemId)).thenReturn(Optional.of(context));
		when(categoryService.getValidSubReference(newSubCategoryId)).thenReturn(hammer);
		when(itemRepository.updateItemSubCategory(
		        itemId, validatedNewSubCatId, context.currentSubCategoryId(), context.status()))
		.thenReturn(1);
		
		itemService.changeSubCategory(itemId, newSubCategoryId);

		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verify(itemRepository).updateItemSubCategory(itemId, validatedNewSubCatId, context.currentSubCategoryId(), context.status());
	}
	
	@Test
	void shouldThrowWhenConcurrentChangeSubCategory() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newSubCategoryId = hammer.getId();	
		String validatedNewSubCatId = hammer.getId();

		ChangeItemSubCategoryContext context = ItemTestFactory.toChangeSubCategoryContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeSubCategoryContext(itemId)).thenReturn(Optional.of(context));
		when(categoryService.getValidSubReference(newSubCategoryId)).thenReturn(hammer);
		when(itemRepository.updateItemSubCategory(
		        itemId, validatedNewSubCatId, context.currentSubCategoryId(), context.status()))
		.thenReturn(0);
		
		assertThatThrownBy(() -> itemService.changeSubCategory(itemId, newSubCategoryId))
		.isInstanceOf(ApiException.class);

		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verify(itemRepository).updateItemSubCategory(
		        itemId, validatedNewSubCatId, context.currentSubCategoryId(), context.status());
	}
	
	@Test
	void shouldDoNothingWhenItemSubCategoryEqualsNewSubCategory() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newSubCategoryId = drill.getId();	

		ChangeItemSubCategoryContext context = ItemTestFactory.toChangeSubCategoryContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeSubCategoryContext(itemId)).thenReturn(Optional.of(context));

		itemService.changeSubCategory(itemId, newSubCategoryId);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenSubCategoryNotFound() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newSubCategoryId = "invalid-id";	

		ChangeItemSubCategoryContext context = ItemTestFactory.toChangeSubCategoryContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeSubCategoryContext(itemId)).thenReturn(Optional.of(context));
		doThrow(new ApiException(SubCategoryErrorType.NOT_FOUND))
		.when(categoryService).getValidSubReference(newSubCategoryId);
		
		assertThatThrownBy(() -> itemService.changeSubCategory(itemId, newSubCategoryId))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenUserIsNotOwnerOnChangeSubCategory() {
		String currentUserId = owner2.getId();
		String itemId = item.getId();
		String newSubCategoryId = hammer.getId();	

		ChangeItemSubCategoryContext context = ItemTestFactory.toChangeSubCategoryContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeSubCategoryContext(itemId)).thenReturn(Optional.of(context));
		doThrow(new ApiException(ItemErrorType.OWNER_REQUIRED))
		.when(authorizationService).requireOwner(context.ownerId(), currentUserId);

		assertThatThrownBy(() -> itemService.changeSubCategory(itemId, newSubCategoryId))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenItemIsBlockedOnChangeSubCategory() {
		String currentUserId = owner.getId();
		String itemId = item.getId();
		String newSubCategoryId = hammer.getId();	

		ChangeItemSubCategoryContext context = ItemTestFactory.toChangeSubCategoryContext(item);
		
		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeSubCategoryContext(itemId)).thenReturn(Optional.of(context));
		doThrow(new ApiException(ItemErrorType.BLOCKED))
		.when(authorizationService).requireNotBlocked(context.status());

		assertThatThrownBy(() -> itemService.changeSubCategory(itemId, newSubCategoryId))
		.isInstanceOf(ApiException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verifyNoMoreInteractions(authorizationService);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldChangeAvailabilityFromAvailableToUnavailable() {
	    String currentUserId = owner.getId();
	    String itemId = item.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId)).thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, context.currentStatus(), ItemStatus.UNAVAILABLE))
	            .thenReturn(1);

	    itemService.changeAvailability(itemId);

	    verify(currentUserProvider).currentUserId();
	    verify(authorizationService).validateItemFromDB(itemId, currentUserId);
	    verify(itemRepository).updateStatus(itemId, context.currentStatus(), ItemStatus.UNAVAILABLE);
	}
	
	@Test
	void shouldChangeAvailabilityFromUnavailableToAvailable() {
	    String currentUserId = owner.getId();

	    Item unavailableItem = ItemTestFactory.createPersisted(
	            owner, ownerAddress, drill, "200", ItemCondition.NEW);
	    unavailableItem.setItemStatus(ItemStatus.UNAVAILABLE);

	    String itemId = unavailableItem.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(unavailableItem, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId)).thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, context.currentStatus(), ItemStatus.AVAILABLE))
	            .thenReturn(1);

	    itemService.changeAvailability(itemId);

	    verify(currentUserProvider).currentUserId();
	    verify(authorizationService).validateItemFromDB(itemId, currentUserId);
	    verify(itemRepository).updateStatus(itemId, context.currentStatus(), ItemStatus.AVAILABLE);
	}
	
	@Test
	void shouldThrowWhenConcurrentChangeAvailability() {
	    String currentUserId = owner.getId();
	    String itemId = item.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId)).thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, context.currentStatus(), ItemStatus.UNAVAILABLE))
	            .thenReturn(0);

	    assertThatThrownBy(() -> itemService.changeAvailability(itemId))
	            .isInstanceOf(ApiException.class);

	    verify(currentUserProvider).currentUserId();
	    verify(authorizationService).validateItemFromDB(itemId, currentUserId);
	    verify(itemRepository).updateStatus(itemId, context.currentStatus(), ItemStatus.UNAVAILABLE);
	}
	
	@Test
	void shouldThrowWhenUserCannotChangeAvailability() {
	    String currentUserId = owner2.getId();
	    String itemId = item.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner2);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId)).thenReturn(Optional.of(context));

	    doThrow(new ApiException(ItemErrorType.OWNER_REQUIRED))
	            .when(authorizationService)
	            .validateItemFromDB(itemId, currentUserId);

	    assertThatThrownBy(() -> itemService.changeAvailability(itemId))
	            .isInstanceOf(ApiException.class);

	    verify(currentUserProvider).currentUserId();
	    verify(authorizationService).validateItemFromDB(itemId, currentUserId);
	    verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenItemStatusCannotChangeAvailability() {
	    String currentUserId = owner.getId();

	    item.setItemStatus(ItemStatus.RENTED);

	    String itemId = item.getId();
	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId)).thenReturn(Optional.of(context));

	    assertThatThrownBy(() -> itemService.changeAvailability(itemId))
	            .isInstanceOf(ApiException.class)
	            .hasFieldOrPropertyWithValue("errorCode", ItemErrorType.CHANGE_AVAILABILITY_ERROR.getErrorCode());

	    verifyNoInteractions(authorizationService);
	    verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenChangeSubCategoryContextNotFound() {
		String currentUserId = owner.getId();
		String itemId = "invalid-id";
		String newSubCategoryId = hammer.getId();

		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getChangeSubCategoryContext(itemId))
			.thenReturn(Optional.empty());

		assertThatThrownBy(() -> itemService.changeSubCategory(itemId, newSubCategoryId))
			.isInstanceOf(ApiException.class);

		verify(currentUserProvider).currentUserId();
		verifyNoInteractions(authorizationService, itemRepository);
	}
	
	@Test
	void shouldMarkItemAsRented() {
	    String itemId = item.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, context.currentStatus(), ItemStatus.RENTED))
	            .thenReturn(1);

	    itemService.markRentedItem(itemId);

	    verify(authorizationService).requireNotBlocked(context.currentStatus());
	    verify(itemRepository)
	            .updateStatus(itemId, context.currentStatus(), ItemStatus.RENTED);
	}
	
	@Test
	void shouldThrowWhenConcurrentMarkRentedItem() {
	    String itemId = item.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, context.currentStatus(), ItemStatus.RENTED))
	            .thenReturn(0);

	    assertThatThrownBy(() -> itemService.markRentedItem(itemId))
	            .isInstanceOf(ApiException.class);

	    verify(authorizationService).requireNotBlocked(context.currentStatus());
	    verify(itemRepository)
	            .updateStatus(itemId, context.currentStatus(), ItemStatus.RENTED);
	}

	@Test
	void shouldThrowWhenItemIsBlockedOnMarkRented() {
	    String itemId = item.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));

	    doThrow(new ApiException(ItemErrorType.BLOCKED))
	            .when(authorizationService)
	            .requireNotBlocked(context.currentStatus());

	    assertThatThrownBy(() -> itemService.markRentedItem(itemId))
	            .isInstanceOf(ApiException.class);

	    verify(authorizationService).requireNotBlocked(context.currentStatus());
	    verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenTransitionToRentedIsInvalid() {
	    item.setItemStatus(ItemStatus.ANALISYS);

	    String itemId = item.getId();
	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));

	    assertThatThrownBy(() -> itemService.markRentedItem(itemId))
	            .isInstanceOf(ApiException.class)
	            .hasFieldOrPropertyWithValue(
	                    "errorCode",
	                    ItemErrorType.INVALID_STATUS_TRANSITION.getErrorCode());

	    verify(authorizationService).requireNotBlocked(context.currentStatus());
	    verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldRecalculateAvailabilityToUnavailable() {
	    String itemId = item.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, ItemStatus.AVAILABLE, ItemStatus.UNAVAILABLE))
	            .thenReturn(1);

	    itemService.recalculateAvailability(itemId, RentalStatus.CONFIRMED);

	    verify(itemRepository)
	            .updateStatus(itemId, ItemStatus.AVAILABLE, ItemStatus.UNAVAILABLE);
	}
	
	@Test
	void shouldRecalculateAvailabilityToAvailableWhenRentalIsCancelled() {
	    Item unavailableItem = ItemTestFactory.createPersisted(
	            owner, ownerAddress, drill, "200", ItemCondition.NEW);
	    unavailableItem.setItemStatus(ItemStatus.UNAVAILABLE);

	    String itemId = unavailableItem.getId();
	    UpdateItemStatusContext context =
	            ItemTestFactory.toUpdateItemStatusContext(unavailableItem, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, ItemStatus.UNAVAILABLE, ItemStatus.AVAILABLE))
	            .thenReturn(1);

	    itemService.recalculateAvailability(itemId, RentalStatus.CANCELLED);

	    verify(itemRepository)
	            .updateStatus(itemId, ItemStatus.UNAVAILABLE, ItemStatus.AVAILABLE);
	}
	
	@Test
	void shouldRecalculateAvailabilityToAvailableWhenRentalExpired() {
	    Item unavailableItem = ItemTestFactory.createPersisted(
	            owner, ownerAddress, drill, "200", ItemCondition.NEW);
	    unavailableItem.setItemStatus(ItemStatus.UNAVAILABLE);

	    String itemId = unavailableItem.getId();
	    UpdateItemStatusContext context =
	            ItemTestFactory.toUpdateItemStatusContext(unavailableItem, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, ItemStatus.UNAVAILABLE, ItemStatus.AVAILABLE))
	            .thenReturn(1);

	    itemService.recalculateAvailability(itemId, RentalStatus.EXPIRED);

	    verify(itemRepository)
	            .updateStatus(itemId, ItemStatus.UNAVAILABLE, ItemStatus.AVAILABLE);
	}
	
	@Test
	void shouldNotUpdateWhenItemAlreadyHasExpectedStatus() {
	    Item unavailableItem = ItemTestFactory.createPersisted(
	            owner, ownerAddress, drill, "200", ItemCondition.NEW);
	    unavailableItem.setItemStatus(ItemStatus.UNAVAILABLE);

	    String itemId = unavailableItem.getId();
	    UpdateItemStatusContext context =
	            ItemTestFactory.toUpdateItemStatusContext(unavailableItem, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));

	    itemService.recalculateAvailability(itemId, RentalStatus.IN_USE);

	    verify(itemRepository, never())
	            .updateStatus(any(), any(), any());
	}
	
	@Test
	void shouldNotUpdateWhenItemIsAlreadyAvailable() {
	    String itemId = item.getId();

	    UpdateItemStatusContext context =
	            ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));

	    itemService.recalculateAvailability(itemId, RentalStatus.CANCELLED);

	    verify(itemRepository, never())
	            .updateStatus(any(), any(), any());
	}
	
	@Test
	void shouldBlockItemWhenOwnerIsBanned() {
	    Item bannedOwnerItem = ItemTestFactory.createPersisted(
	            owner, ownerAddress, drill, "200", ItemCondition.NEW);
	    owner.setUserStatus(UserStatus.BANNED);
	    UpdateItemStatusContext context = new UpdateItemStatusContext(
	    		bannedOwnerItem.getId(),
	    		bannedOwnerItem.getItemStatus(),
	            owner.getId(),
	            UserStatus.BANNED);

	    String itemId = bannedOwnerItem.getId();

	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, context.currentStatus(), ItemStatus.BLOCKED))
	            .thenReturn(1);

	    itemService.recalculateAvailability(itemId, RentalStatus.CANCELLED);

	    verify(itemRepository)
	            .updateStatus(itemId, context.currentStatus(), ItemStatus.BLOCKED);
	}
	
	@Test
	void shouldApproveItem() {
	    String currentUserId = owner.getId();
	    String itemId = item.getId();

	    item.setItemStatus(ItemStatus.ANALISYS);

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, ItemStatus.ANALISYS, ItemStatus.AVAILABLE))
	            .thenReturn(1);

	    itemService.approveItem(itemId);

	    verify(itemRepository)
	            .updateStatus(itemId, ItemStatus.ANALISYS, ItemStatus.AVAILABLE);

	    ArgumentCaptor<ItemApprovedEvent> captor =
	            ArgumentCaptor.forClass(ItemApprovedEvent.class);

	    verify(eventPublisher).publish(captor.capture());

	    assertThat(captor.getValue().entityId()).isEqualTo(itemId);
	    assertThat(captor.getValue().actorId()).isEqualTo(currentUserId);
	}

	@Test
	void shouldThrowWhenApprovingItemThatIsNotUnderAnalysis() {
	    String currentUserId = owner.getId();
	    String itemId = item.getId();

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));

	    assertThatThrownBy(() -> itemService.approveItem(itemId))
	            .isInstanceOf(ApiException.class);

	    verify(itemRepository, never()).updateStatus(any(), any(), any());
	    verifyNoInteractions(eventPublisher);
	}

	@Test
	void shouldThrowWhenConcurrentApproveItem() {
	    String currentUserId = owner.getId();
	    String itemId = item.getId();

	    item.setItemStatus(ItemStatus.ANALISYS);

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, ItemStatus.ANALISYS, ItemStatus.AVAILABLE))
	            .thenReturn(0);

	    assertThatThrownBy(() -> itemService.approveItem(itemId))
	            .isInstanceOf(ApiException.class);

	    verify(eventPublisher, never()).publish(any());
	}

	@Test
	void shouldRejectItem() {
	    String currentUserId = owner.getId();
	    String itemId = item.getId();

	    item.setItemStatus(ItemStatus.ANALISYS);

	    ItemRejectedRequestDto dto =
	            new ItemRejectedRequestDto(ItemRejectionReason.INVALID_TITLE);

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, ItemStatus.ANALISYS, ItemStatus.BLOCKED))
	            .thenReturn(1);

	    itemService.rejectItem(itemId, dto);

	    verify(itemRepository)
	            .updateStatus(itemId, ItemStatus.ANALISYS, ItemStatus.BLOCKED);

	    ArgumentCaptor<ItemRejectedEvent> captor =
	            ArgumentCaptor.forClass(ItemRejectedEvent.class);

	    verify(eventPublisher).publish(captor.capture());

	    assertThat(captor.getValue().entityId()).isEqualTo(itemId);
	    assertThat(captor.getValue().actorId()).isEqualTo(currentUserId);
	}

	@Test
	void shouldThrowWhenRejectingItemThatIsNotUnderAnalysis() {
	    String currentUserId = owner.getId();
	    String itemId = item.getId();

	    ItemRejectedRequestDto dto =
	            new ItemRejectedRequestDto(ItemRejectionReason.INVALID_TITLE);

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));

	    assertThatThrownBy(() -> itemService.rejectItem(itemId, dto))
	            .isInstanceOf(ApiException.class);

	    verify(itemRepository, never()).updateStatus(any(), any(), any());
	    verifyNoInteractions(eventPublisher);
	}

	@Test
	void shouldThrowWhenConcurrentRejectItem() {
	    String currentUserId = owner.getId();
	    String itemId = item.getId();

	    item.setItemStatus(ItemStatus.ANALISYS);

	    ItemRejectedRequestDto dto =
	            new ItemRejectedRequestDto(ItemRejectionReason.INVALID_TITLE);

	    UpdateItemStatusContext context = ItemTestFactory.toUpdateItemStatusContext(item, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
	    when(queryRepository.getUpdateStatusContext(itemId))
	            .thenReturn(Optional.of(context));
	    when(itemRepository.updateStatus(itemId, ItemStatus.ANALISYS, ItemStatus.BLOCKED))
	            .thenReturn(0);

	    assertThatThrownBy(() -> itemService.rejectItem(itemId, dto))
	            .isInstanceOf(ApiException.class);

	    verify(eventPublisher, never()).publish(any());
	}
}
