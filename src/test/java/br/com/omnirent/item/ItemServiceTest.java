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
	
	private Address ownerAddress;
	
	private Category tools;
	private SubCategory drill;
	
	private Item item;
	private Item item2;
	
	@BeforeEach
	void setUp() {
		owner = UserTestFactory.persistedOwner();
		ownerAddress = AddressTestFactory.forPersistedUser(owner);
        tools = CategoryTestFactory.createPersisted("Tools");
        drill = SubCategoryTestFactory.createPersisted("Drill", tools);
        
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
		verifyNoMoreInteractions(itemRepository);
	}
	
	@Test
	void shouldThrowWhenItemNotFound() {
		String invalidId = "invalidId";
	
		when(itemRepository.findItemDetailDTO(invalidId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> itemService.getItemById(invalidId))
			.isInstanceOf(ItemNotFoundException.class);
				
		verify(itemRepository).findItemDetailDTO(invalidId);
		verifyNoMoreInteractions(itemRepository);
	}
	
	@Test
	void shouldGetItemRentedContextById() {
		String itemId = item.getId();
		ItemRentedContext context = ItemTestFactory.toItemRentedContext(item, ownerAddress, owner);
	
		when(itemRepository.getItemRentedContext(itemId)).thenReturn(Optional.of(context));
		
		ItemRentedContext result = itemService.getItemRentedContext(itemId);
		
		assertThat(result).isEqualTo(context);
		
		verify(itemRepository).getItemRentedContext(itemId);
		verifyNoMoreInteractions(itemRepository);
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
		verifyNoMoreInteractions(itemRepository, currentUserProvider, userService);
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
		verifyNoMoreInteractions(currentUserProvider, userService);
	}
	
	@Test
	void shouldAddItem() {
		String ownerId = owner.getId();
		
		ItemRequestDTO request = ItemTestFactory.newItemRequest("200", "NEW", drill.getId(), ownerAddress.getId());
		
		Item mappedItem = ItemTestFactory.fromNewItemRequestDTO(request, drill, ownerAddress, owner);
		Item persistedItem = ItemTestFactory.toPersisted(mappedItem);
		ItemCreatedDTO expected = ItemTestFactory.toItemCreatedDTO(persistedItem);
		
		when(currentUserProvider.currentUserId()).thenReturn(ownerId);
		when(userService.getUserReference(ownerId)).thenReturn(owner);
		when(addressService.findById(ownerAddress.getId())).thenReturn(ownerAddress);
		when(categoryService.findSubById(drill.getId())).thenReturn(drill);
		
		when(itemMapper.fromDto(request, owner, ownerId, ownerAddress,
				drill, ItemStatus.AVAILABLE)).thenReturn(mappedItem);
		when(itemRepository.save(mappedItem)).thenReturn(persistedItem);
		when(itemMapper.toCreatedDto(persistedItem)).thenReturn(expected);
		
		ItemCreatedDTO result = itemService.addItem(request);
		
		assertThat(result).isEqualTo(expected);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(ownerId);
		verify(userService).getUserReference(ownerId);
		verify(addressService).findById(ownerAddress.getId());
		verify(categoryService).findSubById(drill.getId());
		verify(itemMapper).fromDto(request, owner, ownerId, ownerAddress, drill, ItemStatus.AVAILABLE);
		verify(itemRepository).save(mappedItem);
		verify(itemMapper).toCreatedDto(persistedItem);
		verifyNoMoreInteractions(currentUserProvider, userService, addressService,
				categoryService, itemMapper, itemRepository);
	}
	
	@Test
	void shouldThrowWhenUserNotFoundOnAddItem() {
		String invalidId = "invalidId";
		
		ItemRequestDTO request = ItemTestFactory.newItemRequest("200", "NEW", drill.getId(), ownerAddress.getId());
		
		when(currentUserProvider.currentUserId()).thenReturn(invalidId);
		doThrow(UserNotFoundException.class).when(userService).requireExistence(invalidId);
		
		assertThatThrownBy(() -> itemService.addItem(request))
		.isInstanceOf(UserNotFoundException.class);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(invalidId);
		verifyNoInteractions(addressService, categoryService, itemMapper, itemRepository);
		verifyNoMoreInteractions(userService, currentUserProvider);
	}
}
