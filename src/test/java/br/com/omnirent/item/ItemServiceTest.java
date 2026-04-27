package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.address.AddressService;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.exception.common.ForbiddenException;
import br.com.omnirent.exception.domain.AddressNotFoundException;
import br.com.omnirent.exception.domain.ItemNotFoundException;
import br.com.omnirent.exception.domain.SubCategoryNotFoundException;
import br.com.omnirent.exception.domain.UserNotFoundException;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
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
		
		ItemDetailDTO result = itemService.getItemById(itemId);
		
		assertThat(result).isEqualTo(itemDetailDTO);
		
		verify(queryRepository).findItemDetailDTO(itemId);
	}
	
	@Test
	void shouldThrowWhenItemNotFound() {
		String invalidId = "invalidId";
	
		when(queryRepository.findItemDetailDTO(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> itemService.getItemById(invalidId))
			.isInstanceOf(ItemNotFoundException.class);
				
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
			.isInstanceOf(ItemNotFoundException.class);
				
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
		doThrow(UserNotFoundException.class).when(userService).requireExistence(invalidId);
	
		assertThatThrownBy(() -> itemService.getUserItems())
		.isInstanceOf(UserNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(invalidId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldAddItem() {
		String ownerId = owner.getId();
		String addressId = ownerAddress.getId();
		String categoryId = drill.getId();
		
		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", "NEW", categoryId, addressId);
		
		Item mappedItem = ItemTestFactory.fromNewItemRequestDTO(request, drill, ownerAddress, owner);
		Item persistedItem = ItemTestFactory.toPersisted(mappedItem);
		ItemCreatedDTO expected = ItemTestFactory.toItemCreatedDTO(persistedItem);
		
		when(currentUserProvider.currentUserId()).thenReturn(ownerId);
		when(userService.getValidReference(ownerId)).thenReturn(owner);
		when(addressService.getValidReference(addressId, ownerId)).thenReturn(ownerAddress);
		when(categoryService.getValidSubReference(categoryId)).thenReturn(drill);
		
		when(itemMapper.fromDto(request, ownerId, addressId, categoryId, ItemStatus.AVAILABLE))
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
		
		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", "NEW", drill.getId(), ownerAddress.getId());
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);
		doThrow(UserNotFoundException.class).when(userService).getValidReference(invalidId);
		
		assertThatThrownBy(() -> itemService.addItem(request))
		.isInstanceOf(UserNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenAddressNotFoundOnAddItem() {
		String invalidOwnerId = owner.getId();
		String invalidAddressId = "invalidId";

		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", "NEW", drill.getId(), invalidAddressId);
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidOwnerId);
		doThrow(AddressNotFoundException.class).when(addressService).getValidReference(invalidAddressId, invalidOwnerId);
		
		assertThatThrownBy(() -> itemService.addItem(request))
		.isInstanceOf(AddressNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenSubCategoryNotFoundOnAddItem() {
		String ownerId = owner.getId();
		String invalidId = "invalidId";
		
		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", "NEW", invalidId, ownerAddress.getId());
		
		when(currentUserProvider.currentUserId()).thenReturn(ownerId);
		doThrow(SubCategoryNotFoundException.class).when(categoryService).getValidSubReference(invalidId);
		
		assertThatThrownBy(() -> itemService.addItem(request))
		.isInstanceOf(SubCategoryNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldUpdateItem() {
		String currentUserId = owner.getId();
		
		UpdateItemRequestDTO request = ItemTestFactory.updateItemRequest(item.getId(), "200", "USED");
		UpdateItemContext context = ItemTestFactory.updateItemContext(item, currentUserId);

		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getUpdateContext(item.getId())).thenReturn(Optional.of(context));
		when(itemRepository.updateItem(
			    context.itemInfo().getId(), context.status(), request.name(),
			    request.brand(), request.model(), request.description(),
			    request.basePrice(), ItemCondition.fromString(request.itemCondition())
			)).thenReturn(1); 
		
		itemService.updateItem(request);
		
		verify(currentUserProvider).currentUserId();
		verify(authorizationService).requireNotBlocked(context.status());
		verify(authorizationService).requireOwner(context.ownerId(), currentUserId);
		verify(itemRepository).updateItem(
			    context.itemInfo().getId(), context.status(), request.name(),
			    request.brand(), request.model(), request.description(),
			    request.basePrice(), ItemCondition.fromString(request.itemCondition()));
		verifyNoMoreInteractions(itemRepository, authorizationService, currentUserProvider);
	}
	
	@Test
	void shouldThrowWhenItemIsBlocked() {
		String currentUserId = owner.getId();
		
		UpdateItemRequestDTO request = ItemTestFactory.updateItemRequest(item.getId(), "200", "USED");
		UpdateItemContext context = ItemTestFactory.updateItemContext(item, currentUserId);

		when(currentUserProvider.currentUserId()).thenReturn(currentUserId);
		when(queryRepository.getUpdateContext(item.getId())).thenReturn(Optional.of(context));
		doThrow(ForbiddenException.class).when(authorizationService).requireNotBlocked(context.status());
		
		assertThatThrownBy(() -> itemService.updateItem(request))
		.isInstanceOf(ForbiddenException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(queryRepository).getUpdateContext(item.getId());
		verify(authorizationService).requireNotBlocked(context.status());
		verifyNoMoreInteractions(queryRepository, authorizationService, currentUserProvider);
		verifyNoInteractions(itemRepository);
	}
}
