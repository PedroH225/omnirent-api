package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.exception.common.ForbiddenException;
import br.com.omnirent.exception.domain.ItemNotFoundException;
import br.com.omnirent.exception.domain.UserNotFoundException;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
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
	
		when(itemRepository.findItemDetailDTO(itemId)).thenReturn(Optional.of(itemDetailDTO));
		
		ItemDetailDTO result = itemService.getItemById(itemId);
		
		assertThat(result).isEqualTo(itemDetailDTO);
		
		verify(itemRepository).findItemDetailDTO(itemId);
	}
	
	@Test
	void shouldThrowWhenItemNotFound() {
		String invalidId = "invalidId";
	
		when(itemRepository.findItemDetailDTO(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> itemService.getItemById(invalidId))
			.isInstanceOf(ItemNotFoundException.class);
				
		verify(itemRepository).findItemDetailDTO(invalidId);
	}
	
	@Test
	void shouldGetItemRentedContextById() {
		String itemId = item.getId();
		ItemRentedContext context = ItemTestFactory.toItemRentedContext(item, ownerAddress, owner);
	
		when(itemRepository.getItemRentedContext(itemId)).thenReturn(Optional.of(context));
		
		ItemRentedContext result = itemService.getItemRentedContext(itemId);
		
		assertThat(result).isEqualTo(context);
		
		verify(itemRepository).getItemRentedContext(itemId);
	}
	
	@Test
	void shouldThrowWhenItemRentedContextNotFound() {
		String invalidId = "invalidId";
		
		when(itemRepository.getItemRentedContext(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> itemService.getItemRentedContext(invalidId))
			.isInstanceOf(ItemNotFoundException.class);
				
		verify(itemRepository).getItemRentedContext(invalidId);
		verifyNoMoreInteractions(itemRepository);
	}
	
	@Test
	void shouldGetUserItems() {
		String userId = owner.getId();
		
		ItemDisplayDTO dto1 = ItemTestFactory.toItemDisplayDTO(item, drill, owner);
		ItemDisplayDTO dto2 = ItemTestFactory.toItemDisplayDTO(item2, drill, owner);
		List<ItemDisplayDTO> expected = List.of(dto1, dto2);
		
		when(currentUserProvider.currentUserId()).thenReturn(userId);
		when(itemRepository.findUserItems(userId)).thenReturn(expected);

		List<ItemDisplayDTO> result = itemService.getUserItems();
		
		assertThat(result).isEqualTo(expected);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(userId);
		verify(itemRepository).findUserItems(userId);
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
		
		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", "NEW", drill.getId(), ownerAddress.getId());
		
		Item mappedItem = ItemTestFactory.fromNewItemRequestDTO(request, drill, ownerAddress, owner);
		Item persistedItem = ItemTestFactory.toPersisted(mappedItem);
		ItemCreatedDTO expected = ItemTestFactory.toItemCreatedDTO(persistedItem);
		
		when(currentUserProvider.currentUserId()).thenReturn(ownerId);
		when(userService.getUserReference(ownerId)).thenReturn(owner);
		when(addressService.findById(ownerAddress.getId())).thenReturn(ownerAddress);
		when(categoryService.findSubById(drill.getId())).thenReturn(drill);
		
		when(itemMapper.fromDto(request, owner, ownerId, ownerAddress,
				drill, ItemStatus.AVAILABLE)).thenReturn(mappedItem);
		when(itemRepository.save(any(Item.class))).thenReturn(persistedItem);
		when(itemMapper.toCreatedDto(persistedItem)).thenReturn(expected);
		
		ItemCreatedDTO result = itemService.addItem(request);
		
		assertThat(result).isEqualTo(expected);
		
	    verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(ownerId);
		verify(itemRepository).save(mappedItem);
	}
	
	@Test
	void shouldThrowWhenUserNotFoundOnAddItem() {
		String invalidId = "invalidId";
		
		ItemRequestDTO request = ItemTestFactory.createItemRequest(item.getId(), "200", "NEW", drill.getId(), ownerAddress.getId());
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);
		doThrow(UserNotFoundException.class).when(userService).requireExistence(invalidId);
		
		assertThatThrownBy(() -> itemService.addItem(request))
		.isInstanceOf(UserNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(invalidId);
		verifyNoInteractions(itemRepository);
	}
	
	@Test
	void shouldUpdateItemWithoutChangingAddressOrSubCategory() {
	    String ownerId = owner.getId();

	    ItemRequestDTO request = ItemTestFactory.createItemRequest(
	            item.getId(), "250", "USED", drill.getId(), ownerAddress.getId()
	    );

	    ItemDetailDTO expected = ItemTestFactory.toItemDetailsDto(item, drill, ownerAddress, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(ownerId);
	    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
	    when(itemRepository.save(item)).thenReturn(item);
	    when(itemMapper.toDto(item)).thenReturn(expected);

	    ItemDetailDTO result = itemService.updateItem(request);

	    assertThat(result).isEqualTo(expected);

	    verify(currentUserProvider).currentUserId();
	    verify(itemRepository).save(item);
	    verifyNoInteractions(addressService, categoryService);
	}
	
	@Test
	void shouldUpdateItemWithSubCategoryAndAddress() {
		String ownerId = owner.getId();

	    ItemRequestDTO request = ItemTestFactory.createItemRequest(
	            item.getId(), "250", "USED", hammer.getId(), ownerAddress2.getId()
	    );

	    ItemDetailDTO expected = ItemTestFactory.toItemDetailsDto(item, hammer, ownerAddress2, owner);

	    when(currentUserProvider.currentUserId()).thenReturn(ownerId);
	    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
	    when(addressService.findById(ownerAddress2.getId())).thenReturn(ownerAddress2);
	    when(categoryService.findSubById(hammer.getId())).thenReturn(hammer);
	    when(itemRepository.save(item)).thenReturn(item);
	    when(itemMapper.toDto(item)).thenReturn(expected);

	    ItemDetailDTO result = itemService.updateItem(request);

	    assertThat(result).isEqualTo(expected);

	    verify(currentUserProvider).currentUserId();
	    verify(addressService).findById(ownerAddress2.getId());
	    verify(categoryService).findSubById(hammer.getId());
	    verify(itemRepository).save(item);
	}
	
	@Test
	void shouldThrowWhenUserIsNotOwnerOnUpdateItem() {
		String invalidUserId = owner2.getId();

	    ItemRequestDTO request = ItemTestFactory.createItemRequest(
	            item.getId(), "250", "USED", hammer.getId(), ownerAddress2.getId()
	    );

	    when(currentUserProvider.currentUserId()).thenReturn(invalidUserId);
	    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
	    doThrow(ForbiddenException.class).when(authorizationService).requireOwner(item, invalidUserId);

	    assertThatThrownBy(() -> itemService.updateItem(request))
	    .isInstanceOf(ForbiddenException.class);
	    
	    verify(currentUserProvider).currentUserId();
	    verify(itemRepository).findById(item.getId());
	    verify(authorizationService).requireOwner(item, invalidUserId);

	    verifyNoInteractions(addressService, categoryService, itemMapper);
	    verifyNoMoreInteractions(currentUserProvider, itemRepository, authorizationService);
	}
	
	@Test
	void shouldUpdateItemStatus() {
		String ownerId = owner.getId();
		String newStatus = "INACTIVE";
		
	    ItemDetailDTO expected = ItemTestFactory.toItemDetailsDto(item, hammer, ownerAddress2, owner);
		expected.setItemStatus(newStatus);
	    
		when(currentUserProvider.currentUserId()).thenReturn(ownerId);
	    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
	    when(itemRepository.save(any(Item.class)))
	    .thenAnswer(invocation -> invocation.getArgument(0, Item.class));
	    when(itemMapper.toDto(item)).thenReturn(expected);
	    
	    ItemDetailDTO result = itemService.updateStatus(item.getId(), newStatus);
	    
	    assertThat(result).isEqualTo(expected);
	    
	    ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
	    verify(itemRepository).save(itemCaptor.capture());
	    
	    Item updatedItem = itemCaptor.getValue();
	    assertThat(updatedItem.getItemStatus()).isEqualTo(ItemStatus.fromString(newStatus));
	    
	    verify(currentUserProvider).currentUserId();
	    verify(authorizationService).requireOwner(item, ownerId);
	    verifyNoMoreInteractions(itemRepository, currentUserProvider, authorizationService);
	}
}
