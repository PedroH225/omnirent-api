package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.config.CacheTestConfig;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemDetailDTO;
import br.com.omnirent.item.dto.ItemDisplayDTO;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import jakarta.transaction.Transactional;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CacheTestConfig.class)
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
	private Item item2;
	
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
        
        ItemData itemData2 = new ItemData(
        	    "Makita", "HP1640", "Corded hammer drill suitable for masonry and wood",
        	    new BigDecimal("249.90"), ItemCondition.USED
        	);
        item2 = new Item();

        item2.setItemStatus(ItemStatus.AVAILABLE);
        item2.setItemData(itemData2);
        item2.setName("Makita HP1640 Hammer Drill");
        item2.setOwnerId(owner.getId());
        item2.setSubCategoryId(drill.getId());
        item2.setPickupAddressId(ownerAddress.getId());
        	
        item = itemRepository.save(item);
        item2 = itemRepository.save(item2);
	}
	
	@Test
	void shouldFindItemDetailDTO() {
		Optional<ItemDetailDTO> optItem = itemRepository.findItemDetailDTO(item.getId());
		
		assertThat(optItem).isPresent();
		assertThat(optItem.get().getId()).isNotNull();
		assertThat(optItem.get())
	    .satisfies(i -> {
	        // Item
	        assertThat(i.getId()).isEqualTo(item.getId());
	        assertThat(i.getName()).isEqualTo(item.getName());
	        assertThat(i.getItemStatus()).isEqualTo(ItemStatus.AVAILABLE.toString());

	        // ItemData
	        assertThat(i.getBrand()).isEqualTo(item.getItemData().getBrand());
	        assertThat(i.getModel()).isEqualTo(item.getItemData().getModel());
	        assertThat(i.getDescription()).isEqualTo(item.getItemData().getDescription());
	        assertThat(i.getBasePrice()).isEqualByComparingTo(item.getItemData().getBasePrice());
	        assertThat(i.getItemCondition()).isEqualTo(ItemCondition.NEW.toString());

	        // SubCategory + Category
	        assertThat(i.getSubCategory().getName()).isEqualTo(drill.getName());
	        assertThat(i.getSubCategory().getCategory()).isEqualTo(tools.getName());

	        // Address
	        assertThat(i.getPickupAddress().getCity()).isEqualTo(ownerAddress.getAddressData().getCity());
	        assertThat(i.getPickupAddress().getStreet()).isEqualTo(ownerAddress.getAddressData().getStreet());

	        // Owner
	        assertThat(i.getOwner().getUsername()).isEqualTo(owner.getName());

	        // Timestamps
	        assertThat(i.getCreatedAt()).isNotNull();
	        assertThat(i.getUpdatedAt()).isNotNull();
	    });
	}
	
	@Test
	void shouldFindUserItems() {
		List<ItemDisplayDTO> userItems = itemRepository.findUserItems(owner.getId());
		
		assertThat(userItems).hasSize(2)
		.allSatisfy(i -> {
	        assertThat(i.getId()).isNotNull();
	        assertThat(i.getName()).isNotNull();
	        assertThat(i.getSubCategoryName()).isNotNull();
	        assertThat(i.getItemCondition()).isNotNull();
	    });;
	    
		assertThat(userItems)
	    .extracting(ItemDisplayDTO::getName)
	    .containsExactlyInAnyOrder(
	        item.getName(),
	        item2.getName());
		
		assertThat(userItems)
	    .extracting(ItemDisplayDTO::getSubCategoryName)
	    .containsOnly(drill.getName());
		
		assertThat(userItems)
		.extracting(item -> item.getOwner().getId())
		.containsOnly(owner.getId());
	}
	
	@Test
	void shouldGetItemRentedContext() {
	    Optional<ItemRentedContext> optContext = itemRepository.getItemRentedContext(item.getId());

	    assertThat(optContext).isPresent();
	    assertThat(optContext.get())
	        .satisfies(context -> {
	            assertThat(context.getOwnerId()).isEqualTo(owner.getId());
	            assertThat(context.getOwnerName()).isEqualTo(owner.getName());
	        
	            assertThat(context.getItemInfo()).isNotNull();
	            assertThat(context.getItemInfo())
	                .satisfies(itemInfo -> {
	                    assertThat(itemInfo.getId()).isEqualTo(item.getId());
	                    assertThat(itemInfo.getItemName()).isEqualTo(item.getName());
	                    assertThat(itemInfo.getBrand()).isEqualTo(item.getItemData().getBrand());
	                    assertThat(itemInfo.getModel()).isEqualTo(item.getItemData().getModel());
	                    assertThat(itemInfo.getDescription()).isEqualTo(item.getItemData().getDescription());
	                    assertThat(itemInfo.getBasePrice()).isEqualByComparingTo(item.getItemData().getBasePrice());
	                    assertThat(itemInfo.getItemCondition()).isEqualTo(item.getItemData().getItemCondition());
	                });

	            assertThat(context.getAddressInfo()).isNotNull();
	            assertThat(context.getAddressInfo())
	                .satisfies(addressInfo -> {
	                    assertThat(addressInfo.getId()).isEqualTo(ownerAddress.getId());
	                    assertThat(addressInfo.getStreet()).isEqualTo(ownerAddress.getAddressData().getStreet());
	                    assertThat(addressInfo.getNumber()).isEqualTo(ownerAddress.getAddressData().getNumber());
	                    assertThat(addressInfo.getComplement()).isEqualTo(ownerAddress.getAddressData().getComplement());
	                    assertThat(addressInfo.getDistrict()).isEqualTo(ownerAddress.getAddressData().getDistrict());
	                    assertThat(addressInfo.getCity()).isEqualTo(ownerAddress.getAddressData().getCity());
	                    assertThat(addressInfo.getState()).isEqualTo(ownerAddress.getAddressData().getState());
	                    assertThat(addressInfo.getCountry()).isEqualTo(ownerAddress.getAddressData().getCountry());
	                    assertThat(addressInfo.getZipCode()).isEqualTo(ownerAddress.getAddressData().getZipCode());
	                });
	        });
	}
	
}
