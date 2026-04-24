package br.com.omnirent.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.AddressService;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.CategoryService;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.transaction.Transactional;

@Transactional
public class ItemServiceIT extends SpringIntegrationTest {

	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private SubCategoryRepository subRepository;
	
	@Autowired
	private ItemService itemService;
	
	private User owner;
	private User owner2;

	private Address ownerAddress;
	private Address ownerAddress2;
	private Address owner2Address;

	private Category electronics;
	private Category sports;

	private SubCategory mouse;
	private SubCategory notebook;
	private SubCategory ball;

	private Item item;
	private Item item2;
	
	@BeforeEach
	void setUp() {
		owner = userRepository.save(UserTestFactory.user());
	    owner2 = userRepository.save(UserTestFactory.user());
	
		ownerAddress = addressRepository.save(AddressTestFactory.forUser(owner));
		ownerAddress2 = addressRepository.save(AddressTestFactory.forUser(owner));
		owner2Address = addressRepository.save(AddressTestFactory.forUser(owner2));
	    
        electronics = categoryRepository.save(CategoryTestFactory.create("Electronics"));
        sports = categoryRepository.save(CategoryTestFactory.create("Sports"));

        notebook = subRepository.save(SubCategoryTestFactory.create("Notebook", electronics));
        mouse = subRepository.save(SubCategoryTestFactory.create("Mouse", electronics));
        ball = subRepository.save(SubCategoryTestFactory.create("Ball", sports));
		
        item = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, mouse, "200", ItemCondition.NEW));
        item2 = itemRepository.save(ItemTestFactory.create(owner, ownerAddress2, notebook, "200", ItemCondition.USED));

		SecurityTestUtils.setAuthenticatedUser(owner.getId());
	}
	
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}
	
	@Test
	void test() {
		System.out.println("owner id: " + owner.getId());
		System.out.println("owner2 id: " + owner2.getId());

		System.out.println("ownerAddress id: " + ownerAddress.getId());
		System.out.println("ownerAddress2 id: " + ownerAddress2.getId());
		System.out.println("owner2Address id: " + owner2Address.getId());

		System.out.println("electronics id: " + electronics.getId());
		System.out.println("sports id: " + sports.getId());

		System.out.println("notebook subCategory id: " + notebook.getId());
		System.out.println("mouse subCategory id: " + mouse.getId());
		System.out.println("ball subCategory id: " + ball.getId());

		System.out.println("item id: " + item.getId());
		System.out.println("item2 id: " + item2.getId());
	}
	
}
