package br.com.omnirent.item;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class ItemRepositoryTest extends IntegrationTest {

	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private SubCategoryRepository subRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;

	private User owner;
	
	private Address ownerAddress;
	
	private Category tools;
	private SubCategory drill;
	
	private Item item;
	
	@BeforeAll
	void setUp() {
		owner = new User("owner", "owner", "owner@email.com", "owner", LocalDate.now(), 1, 1);
		owner = userRepository.save(owner);
		
		AddressData addressData = new AddressData(
			    "Rua Azul", "450", "Apto 12",
			    "Centro", "Campinas", "SP",
			    "Brazil", "13000-000"
			);		
		ownerAddress = new Address();
		ownerAddress.setAddressData(addressData);
		ownerAddress.setUserId(owner.getId());
		ownerAddress = addressRepository.save(ownerAddress);
		
		tools = new Category();
        tools.setName("Tools");
        tools = categoryRepository.save(tools);
        
        drill = new SubCategory();
        drill.setName("Drill");
        drill.setCategory(tools);
        drill = subRepository.save(drill);
        
        ItemData itemData = new ItemData(
        	    "Bosch", "GSB 13 RE", "Corded drill for home and professional use",
        	    new BigDecimal("199.90"), ItemCondition.NEW
        	);
        item = new Item();
        
        item.setItemStatus(ItemStatus.AVAILABLE);
        item.setItemData(itemData);
        item.setName("Bosch GSB 13 RE Corded Drill");
        item.setOwnerId(owner.getId());
        item.setSubCategoryId(drill.getId());
        item.setPickupAddressId(ownerAddress.getId());
        item = itemRepository.save(item);
	}
	
	@Test
	void shouldTest() {
		
	}
	
}
