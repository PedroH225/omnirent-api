package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.address.AddressService;
import br.com.omnirent.address.context.AddressInfo;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.exception.domain.ItemNotFoundException;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserResponseDTO;

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
}
