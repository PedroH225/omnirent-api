package br.com.omnirent.item;

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
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.item.domain.Item;
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
	void test() {
		System.out.println("ownerId: " + owner.getId());
		System.out.println("ownerAddress: " + ownerAddress.getId());
		System.out.println("toolsId:" + tools.getId());
		System.out.println("drillId: " + drill.getId());
		System.out.println("item1Id: " + item.getId());
		System.out.println("item2Id: " + item2.getId());
	}
}
