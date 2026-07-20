package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemRejectionReason;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringMvcIntegration;
import br.com.omnirent.item.context.ItemRejectedRequestDto;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.dto.ItemCreatedDTO;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
public class ItemMvcIT extends SpringMvcIntegration {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private SubCategoryRepository subRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());
	
	private static final String ITEM_PREFIX = "/item";
	
	private User user1;
	
	private Item item1;
	
	private Address address1;
	
	private Category electronics;
	private SubCategory notebook;
	
	@BeforeEach
	void setUp() {
		user1 = userRepository.save(UserTestFactory.owner());
		address1 = addressRepository.save(AddressTestFactory.forUser(user1));
		electronics = categoryRepository.save(CategoryTestFactory.create("electronics"));
		notebook = subRepository.save(SubCategoryTestFactory.create("notebook", electronics));
		
		item1 = itemRepository.save(ItemTestFactory.create(user1, address1, notebook, "200", ItemCondition.NEW));
	    
		SecurityTestUtils.setAuthenticatedUser(user1);
	}
	
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}
	
	@Test
	void shouldSanitizeNewItemFields() throws Exception {
		ItemRequestDTO dirty = new ItemRequestDTO(
		        null,
		        "  Item   Premium  ",
		        "  MODEL    X  ",
		        "  BRAND    NAME  ",
		        "  Descrição    com    vários    espaços  ",
		        new BigDecimal("100.00"),
		        ItemCondition.NEW,
		        String.format("   %s    ", notebook.getId()),
		        String.format("   %s    ", address1.getId())
		);
		
		String payload = objectMapper.writeValueAsString(dirty);

		String response = mockMvc.perform(post(ITEM_PREFIX)
				.with(SecurityTestUtils.auth(user1))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();
	
		ItemCreatedDTO itemDTO = objectMapper.readValue(response, ItemCreatedDTO.class);
		
		Item persistedItem = itemRepository.findById(itemDTO.getId()).orElseThrow();
	
		ItemData itemData = persistedItem.getItemData();
	
		assertThat(persistedItem.getName())
		        .isEqualTo("Item Premium");
	
		assertThat(itemData.getBrand())
		        .isEqualTo("BRAND NAME");
	
		assertThat(itemData.getModel())
		        .isEqualTo("MODEL X");
	
		assertThat(itemData.getDescription())
		        .isEqualTo("Descrição    com    vários    espaços");
	
		assertThat(itemData.getBasePrice())
		        .isEqualByComparingTo("100.00");
	
		assertThat(itemData.getItemCondition())
		        .isEqualTo(ItemCondition.NEW);
	
		assertThat(persistedItem.getSubCategoryId())
		        .isEqualTo(notebook.getId());
	
		assertThat(persistedItem.getPickupAddressId())
		        .isEqualTo(address1.getId());
	}
	
	@Test
	void shouldThrowAfterSanitizeNewItemFields() throws Exception {
		ItemRequestDTO dirty = new ItemRequestDTO(
		        null,
		        "      a     ",
		        "  MODEL    X  ",
		        "  BRAND    NAME  ",
		        "  Descrição    com    vários    espaços  ",
		        new BigDecimal("100.00"),
		        ItemCondition.NEW,
		        String.format("   %s    ", notebook.getId()),
		        String.format("   %s    ", address1.getId())
		);
		
		String payload = objectMapper.writeValueAsString(dirty);

		mockMvc.perform(post(ITEM_PREFIX)
				.with(SecurityTestUtils.auth(user1))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
		.andExpect(status().isConflict());
	}
	
	@Test
	void shouldSanitizeUpdatedItemFields() throws Exception {
		UpdateItemRequestDTO dirty = new UpdateItemRequestDTO(
		        String.format("   %s     ", item1.getId()),
		        "  Item   Premium  ",
		        "  MODEL    X  ",
		        "  BRAND    NAME  ",
		        "  Descrição    com    vários    espaços  ",
		        new BigDecimal("100.00"),
		        ItemCondition.NEW
		);
		
		String payload = objectMapper.writeValueAsString(dirty);

		mockMvc.perform(put(ITEM_PREFIX)
				.with(SecurityTestUtils.auth(user1))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
		.andExpect(status().isOk());
			
		entityManager.flush();
		entityManager.clear();
		
		Item persistedItem = itemRepository.findById(item1.getId()).orElseThrow();
	
		ItemData itemData = persistedItem.getItemData();
	
		assertThat(persistedItem.getName())
		        .isEqualTo("Item Premium");
	
		assertThat(itemData.getBrand())
		        .isEqualTo("BRAND NAME");
	
		assertThat(itemData.getModel())
		        .isEqualTo("MODEL X");
	
		assertThat(itemData.getDescription())
		        .isEqualTo("Descrição    com    vários    espaços");
	
		assertThat(itemData.getBasePrice())
		        .isEqualByComparingTo("100.00");
	
		assertThat(itemData.getItemCondition())
		        .isEqualTo(ItemCondition.NEW);
	
		assertThat(persistedItem.getSubCategoryId())
		        .isEqualTo(notebook.getId());
	
		assertThat(persistedItem.getPickupAddressId())
		        .isEqualTo(address1.getId());
	}
	
	@Test
	void shouldThrowAfterSanitizeUpdatedItemFields() throws Exception {
		UpdateItemRequestDTO dirty = new UpdateItemRequestDTO(
		        String.format("   %s     ", item1.getId()),
		        "      a     ",
		        "  MODEL    X  ",
		        "  BRAND    NAME  ",
		        "  Descrição    com    vários    espaços  ",
		        new BigDecimal("100.00"),
		        ItemCondition.NEW
		);
		
		String payload = objectMapper.writeValueAsString(dirty);

		mockMvc.perform(put(ITEM_PREFIX)
				.with(SecurityTestUtils.auth(user1))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
		.andExpect(status().isConflict());
	}
	
	@Test
	void shouldApproveItemWhenUserIsAdmin() throws Exception {
		User admin = userRepository.save(UserTestFactory.admin());
		item1.setItemStatus(ItemStatus.ANALISYS);
		itemRepository.saveAndFlush(item1);
		
		mockMvc.perform(patch("/item/approve/{itemId}", item1.getId())
	            .with(SecurityTestUtils.auth(admin)))
	        .andExpect(status().isOk());
	}
	
	@Test
	void shouldRejectItemWhenUserIsAdmin() throws Exception {
	    ItemRejectedRequestDto dto =
	            new ItemRejectedRequestDto(ItemRejectionReason.INVALID_TITLE);
		User admin = userRepository.save(UserTestFactory.admin());
		item1.setItemStatus(ItemStatus.ANALISYS);
		itemRepository.saveAndFlush(item1);
		
	    mockMvc.perform(patch("/item/reject/{itemId}", item1.getId())
	            .with(SecurityTestUtils.auth(admin))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(dto)))
	        .andExpect(status().isOk());
	}
	
	@Test
	void shouldReturnForbiddenWhenApprovingItemAsRegularUser() throws Exception {
	    mockMvc.perform(post("/item/approve/{itemId}", item1.getId())
	            .with(SecurityTestUtils.auth(user1)))
	        .andExpect(status().isForbidden());
	}
	
	@Test
	void shouldReturnForbiddenWhenRejectingItemAsRegularUser() throws Exception {
	    ItemRejectedRequestDto dto =
	            new ItemRejectedRequestDto(ItemRejectionReason.INVALID_TITLE);

	    mockMvc.perform(post("/item/reject/{itemId}", item1.getId())
	            .with(SecurityTestUtils.auth(user1))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(dto)))
	        .andExpect(status().isForbidden());
	}
	
	@Test
	void shouldReturnUnauthorizedWhenApprovingItemWithoutAuthentication() throws Exception {
	    mockMvc.perform(post("/item/approve/{itemId}", item1.getId()))
	        .andExpect(status().isUnauthorized());
	}
	
	@Test
	void shouldReturnUnauthorizedWhenRejectingItemWithoutAuthentication() throws Exception {
	    ItemRejectedRequestDto dto =
	            new ItemRejectedRequestDto(ItemRejectionReason.INVALID_TITLE);

	    mockMvc.perform(post("/item/reject/{itemId}", item1.getId())
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(dto)))
	        .andExpect(status().isUnauthorized());
	}
}
