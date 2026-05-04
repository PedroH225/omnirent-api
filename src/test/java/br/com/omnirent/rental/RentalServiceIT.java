package br.com.omnirent.rental;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.RentalTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.item.ItemRepository;
import br.com.omnirent.item.ItemService;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.transaction.Transactional;

@Transactional
public class RentalServiceIT extends SpringIntegrationTest {
	@Autowired
	private RentalService rentalService;
	
	@Autowired
	private RentalRepository rentalRepository;
	
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
	
	@Autowired
	private UserService userService;
	
	private User owner;
	private User renter;

	private Address ownerAddress;
	private Address ownerAddress2;
	
	private Category tools;
	private SubCategory drill;

	private Item item;
	private Item item2;
	
	private Rental rental;
	
	@BeforeEach
	void setUp() {
		owner = userRepository.save(UserTestFactory.owner());
		renter = userRepository.save(UserTestFactory.user());

		ownerAddress = addressRepository.save(AddressTestFactory.forUser(owner));
		ownerAddress2 = addressRepository.save(AddressTestFactory.forUser(owner));

        tools = categoryRepository.save(CategoryTestFactory.create("Tools"));
        drill = subRepository.save(SubCategoryTestFactory.create("Drill", tools));

        item = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, drill,
        		"200", ItemCondition.NEW));
        
        item2 = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, drill,
        		"100", ItemCondition.USED));
        
        rental = rentalRepository.save(RentalTestFactory.create(item, owner, renter, ownerAddress2, "4400", 
        		RentalStatus.CREATED, RentalPeriod.MONTHLY, null, null));
	
		SecurityTestUtils.setAuthenticatedUser(owner.getId());
	}
	
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}
	
	@Test
	void test() {
		System.out.println("ownerId = " + owner.getId());
		System.out.println("renterId = " + renter.getId());

		System.out.println("ownerAddressId = " + ownerAddress.getId());
		System.out.println("ownerAddress2Id = " + ownerAddress2.getId());

		System.out.println("toolsId = " + tools.getId());
		System.out.println("drillId = " + drill.getId());

		System.out.println("itemId = " + item.getId());
		System.out.println("item2Id = " + item2.getId());

		System.out.println("rentalId = " + rental.getId());
	}
}
