package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.exception.common.ForbiddenException;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Transactional
public class ItemServiceIT extends SpringIntegrationTest {

	@Autowired
	private ItemRepository itemRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
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
	void shouldAddItem() {
		ItemCondition condition = ItemCondition.NEW;
	    ItemRequestDTO request = ItemTestFactory.createItemRequest(
	        null, "200", "NEW", notebook.getId(), ownerAddress.getId()
	    );

	    ItemCreatedDTO result = itemService.addItem(request);

	    assertThat(result).isNotNull();
	    assertThat(result.getId()).isNotNull();

	    assertThat(result.getBasePrice()).isEqualByComparingTo("200");
	    assertThat(result.getItemCondition()).isEqualTo(condition.toString());

	    Optional<Item> optPersisted = itemRepository.findById(result.getId());
	    assertThat(optPersisted).isPresent();
	    Item persisted = optPersisted.get();
	    
	    assertThat(persisted.getId()).isEqualTo(result.getId());
	    assertThat(persisted.getOwnerId()).isEqualTo(owner.getId());
	    assertThat(persisted.getPickupAddressId()).isEqualTo(ownerAddress.getId());
	    assertThat(persisted.getSubCategoryId()).isEqualTo(notebook.getId());

	    assertThat(persisted.getItemData().getBasePrice())
	        .isEqualByComparingTo("200");
	    assertThat(persisted.getItemData().getItemCondition())
	        .isEqualTo(condition);
	}
	
	@Test
	void shouldUpdateItem() {
		ItemCondition condition = ItemCondition.USED;
	    UpdateItemRequestDTO request = ItemTestFactory.updateItemRequest(item.getId(), "300", "USED");

	    itemService.updateItem(request);
	    
	    entityManager.flush();
	    entityManager.clear();

	    Optional<Item> optPersisted = itemRepository.findById(item.getId());
	    assertThat(optPersisted).isPresent();
	    Item persisted = optPersisted.get();
	    
	    assertThat(persisted.getId()).isEqualTo(item.getId());
	    assertThat(persisted.getOwnerId()).isEqualTo(owner.getId());

	    assertThat(persisted.getItemData().getBasePrice())
	        .isEqualByComparingTo("300");
	    assertThat(persisted.getItemData().getItemCondition())
	        .isEqualTo(condition);

	    assertThat(persisted.getSubCategoryId()).isEqualTo(mouse.getId());
	    assertThat(persisted.getPickupAddressId()).isEqualTo(ownerAddress.getId());
	}
	
	@Test
	void shouldThrowWhenUserIsNotOwnerOnUpdateItem() {
		SecurityTestUtils.setAuthenticatedUser(owner2.getId());
	    UpdateItemRequestDTO request = ItemTestFactory.updateItemRequest(item.getId(), "300", "USED");
	    
	    assertThatThrownBy(() -> itemService.updateItem(request))
	    .isInstanceOf(ForbiddenException.class);
	    
	    entityManager.flush();
	    entityManager.clear();

	    Optional<Item> optPersisted = itemRepository.findById(item.getId());
	    assertThat(optPersisted).isPresent();

	    Item persisted = optPersisted.get();
	    ItemData persistedData = persisted.getItemData();

	    assertThat(persistedData.getBasePrice()).isNotEqualByComparingTo("300");
	    assertThat(persisted.getItemStatus()).isNotEqualTo(ItemCondition.USED);
	    
	    assertThat(persisted.getOwnerId()).isEqualTo(owner.getId());
	}
	
	@Test
	void shouldChangeItemAddress() {
		itemService.changePickupAddress(item.getId(), ownerAddress2.getId());
		
	    entityManager.flush();
	    entityManager.clear();
		
		Optional<Item> optPersisted = itemRepository.findById(item.getId());
	    assertThat(optPersisted).isPresent();
	    Item persisted = optPersisted.get();
	    
	    assertThat(persisted.getId()).isEqualTo(item.getId());
	    assertThat(persisted.getOwnerId()).isEqualTo(owner.getId());

	    assertThat(persisted.getPickupAddressId()).isNotEqualTo(ownerAddress.getId());
	    assertThat(persisted.getPickupAddressId()).isEqualTo(ownerAddress2.getId());	
	}
	
	@Test
	void shouldChangeItemSubCategory() {
		itemService.changeSubCategory(item.getId(), notebook.getId());
		
		entityManager.flush();
	    entityManager.clear();
		
		Optional<Item> optPersisted = itemRepository.findById(item.getId());
	    assertThat(optPersisted).isPresent();
	    Item persisted = optPersisted.get();
	    
	    assertThat(persisted.getId()).isEqualTo(item.getId());
	    assertThat(persisted.getOwnerId()).isEqualTo(owner.getId());
	    
	    assertThat(persisted.getSubCategoryId()).isNotEqualTo(mouse.getId());
	    assertThat(persisted.getSubCategoryId()).isEqualTo(notebook.getId());
	}
	
	@Test
	void shouldUpdateItemStatusToUnavailable() {
		itemService.updateStatus(item.getId());
		
		entityManager.flush();
	    entityManager.clear();
		
		Optional<Item> optPersisted = itemRepository.findById(item.getId());
	    assertThat(optPersisted).isPresent();
	    Item persisted = optPersisted.get();
	    
	    assertThat(persisted.getItemStatus()).isEqualTo(ItemStatus.UNAVAILABLE);
	    
	    itemService.updateStatus(item.getId());
	}
	
	@Test
	void shouldUpdateItemStatusToAvailable() {
		Item newItem = ItemTestFactory.create(owner, ownerAddress, ball, "200", ItemCondition.USED);
		newItem.setItemStatus(ItemStatus.UNAVAILABLE);
		Item result = itemRepository.save(newItem);
		
		assertThat(result.getItemStatus()).isEqualTo(ItemStatus.UNAVAILABLE);
		
		itemService.updateStatus(result.getId());
		
		entityManager.flush();
	    entityManager.clear();
		
		Optional<Item> optPersisted = itemRepository.findById(result.getId());
	    assertThat(optPersisted).isPresent();
	    Item persisted = optPersisted.get();
	    
	    assertThat(persisted.getItemStatus()).isEqualTo(ItemStatus.AVAILABLE);	    
	}
}
