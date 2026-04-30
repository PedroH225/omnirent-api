package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
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
import br.com.omnirent.item.context.ChangeItemAddressContext;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.context.UpdateItemContext;
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
	private ItemQueryRepository queryRepository;
	
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
	
	@BeforeEach
	void setUp() {
		owner = userRepository.save(UserTestFactory.owner());
		ownerAddress = addressRepository.save(AddressTestFactory.forUser(owner));
        tools = categoryRepository.save(CategoryTestFactory.create("Tools"));
        drill = subRepository.save(SubCategoryTestFactory.create("Drill", tools));
        
        item = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, drill,
        		"200", ItemCondition.NEW));
        
        item2 = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, drill,
        		"100", ItemCondition.USED));
	}
	
	@Test
	void shouldFindItemDetailDTO() {
		Optional<ItemDetailDTO> optItem = queryRepository.findItemDetailDTO(item.getId());
		
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
		List<ItemDisplayDTO> userItems = queryRepository.findUserItems(owner.getId());
		
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
	    Optional<ItemRentedContext> optContext = queryRepository.getItemRentedContext(item.getId());

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
	
	@Test
	void shouldGetUpdateItemContext() {
		Optional<UpdateItemContext> optContext = queryRepository.getUpdateContext(item.getId());
		
		assertThat(optContext).isPresent();
	    assertThat(optContext.get())
	        .satisfies(context -> {
	        	assertThat(context.ownerId()).isEqualTo(item.getOwnerId());
	        	assertThat(context.status()).isEqualTo(item.getItemStatus());
	        	
	        	assertThat(context.itemInfo()).isNotNull();
	        	assertThat(context.itemInfo())
	        	.satisfies(itemInfo -> {
	        		assertThat(itemInfo.getId()).isEqualTo(item.getId());
                    assertThat(itemInfo.getItemName()).isEqualTo(item.getName());
                    assertThat(itemInfo.getBrand()).isEqualTo(item.getItemData().getBrand());
                    assertThat(itemInfo.getModel()).isEqualTo(item.getItemData().getModel());
                    assertThat(itemInfo.getDescription()).isEqualTo(item.getItemData().getDescription());
                    assertThat(itemInfo.getBasePrice()).isEqualByComparingTo(item.getItemData().getBasePrice());
                    assertThat(itemInfo.getItemCondition()).isEqualTo(item.getItemData().getItemCondition());
	        	});
	        });
	}
	
	@Test
	void shouldGetChangeItemAddressContext() {
		Optional<ChangeItemAddressContext> optContext = queryRepository.getChangeAddressContext(item.getId());
		
		assertThat(optContext).isPresent();
	    assertThat(optContext.get())
	    .satisfies(context -> {
	    	assertThat(context.id()).isEqualTo(item.getId());
	    	assertThat(context.ownerId()).isEqualTo(owner.getId());
	    	assertThat(context.currentAddressId()).isEqualTo(ownerAddress.getId());
	    	assertThat(context.status()).isEqualTo(item.getItemStatus());
	    });
	}
	
}
