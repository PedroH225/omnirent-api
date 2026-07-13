package br.com.omnirent.item;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.omnirent.exception.domain.apptype.CommonErrorType;
import br.com.omnirent.integration.SpringMvcIntegration;
import jakarta.transaction.Transactional;
@Transactional
public class ItemFeedMvcIT extends SpringMvcIntegration {

	@Autowired
	private MockMvc mockMvc;
	
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());
	
	private static final String ITEM_FEED_URI = "/item/feed";
	
	
	@Test
	void shouldReturnItemFeed() throws Exception {
		mockMvc.perform(get(ITEM_FEED_URI)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content").exists())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.page").exists())
				.andExpect(jsonPath("$.size").exists())
				.andExpect(jsonPath("$.totalElements").exists())
				.andExpect(jsonPath("$.totalPages").exists())
				.andExpect(jsonPath("$.content[0].id").exists())
				.andExpect(jsonPath("$.content[0].name").exists())
				.andExpect(jsonPath("$.content[0].price.hourPrice").exists())
				.andExpect(jsonPath("$.content[0].owner.username").exists());
	}
	
	@Test
	void shouldFilterFeedByName() throws Exception {
	    mockMvc.perform(get(ITEM_FEED_URI)
	            .param("name", "Canon")
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.totalElements").value(2))
	            .andExpect(jsonPath("$.content", hasSize(2)))
	            .andExpect(jsonPath("$.content[*].name",
	                    everyItem(containsStringIgnoringCase("Canon"))));
	}
	
	@Test
	void shouldFilterFeedBySubCategory() throws Exception {
	    mockMvc.perform(get(ITEM_FEED_URI)
	            .param("subCategory", "CAMERA")
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.totalElements").value(3))
	            .andExpect(jsonPath("$.content", hasSize(3)))
	            .andExpect(jsonPath("$.content[*].subCategoryName",
	                    everyItem(is("CAMERA"))));
	}
	
	@Test
	void shouldFilterFeedByItemCondition() throws Exception {
	    mockMvc.perform(get(ITEM_FEED_URI)
	            .param("itemCondition", "GOOD")
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.totalElements").value(4))
	            .andExpect(jsonPath("$.content", hasSize(4)))
	            .andExpect(jsonPath("$.content[*].itemCondition",
	                    everyItem(is("GOOD"))));
	}
	
	@Test
	void shouldCombineFilters() throws Exception {
	    mockMvc.perform(get(ITEM_FEED_URI)
	            .param("name", "Canon")
	            .param("category", "AUDIOVISUAL")
	            .param("subCategory", "CAMERA")
	            .param("itemCondition", "GOOD")
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.totalElements").value(1))
	            .andExpect(jsonPath("$.content", hasSize(1)))
	            .andExpect(jsonPath("$.content[0].name", is("Canon T6 Camera")))
	            .andExpect(jsonPath("$.content[0].itemCondition", is("GOOD")))
	            .andExpect(jsonPath("$.content[0].subCategoryName", is("CAMERA")));
	}
	
	@Test
	void shouldReturnEmptyFeedWhenNoItemsMatchFilters() throws Exception {
	    mockMvc.perform(get(ITEM_FEED_URI)
	            .param("name", "Playstation 5")
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.content", hasSize(0)))
	            .andExpect(jsonPath("$.totalPages", is(0)))
	            .andExpect(jsonPath("$.totalElements", is(0)));
	}
	
	@Test
	void shouldSortFeedByNewest() throws Exception {
		mockMvc.perform(get(ITEM_FEED_URI)
				.param("sort", "NEWEST")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name", is("Dell XPS 15")));
	}
	
	@Test
	void shouldSortFeedByPriceAsc() throws Exception {
	    mockMvc.perform(get(ITEM_FEED_URI)
	            .param("sort", "PRICE_ASC")
	            .accept(MediaType.APPLICATION_JSON))
	            .andExpect(status().isOk())
	            .andExpect(jsonPath("$.content[0].name", is("Compact Tripod")));
	}
	
	@Test
	void shouldSortFeedByPriceDesc() throws Exception {
		mockMvc.perform(get(ITEM_FEED_URI)
				.param("sort", "PRICE_DESC")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
	            .andExpect(jsonPath("$.content[0].name", is("Event Sound System")));
	}
	
	@Test
	void shouldReturnCalculatedPriceData() throws Exception {
		mockMvc.perform(get(ITEM_FEED_URI)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].price.hourPrice").exists())
				.andExpect(jsonPath("$.content[0].price.dailyPrice").exists())
				.andExpect(jsonPath("$.content[0].price.weeklyPrice").exists())
				.andExpect(jsonPath("$.content[0].price.monthlyPrice").exists());
	}
	
	@Test
	void shouldPaginateFeed() throws Exception {
		mockMvc.perform(get(ITEM_FEED_URI)
				.param("page", "1")
				.param("size", "3")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content", hasSize(3)))
				.andExpect(jsonPath("$.page", is(1)))
				.andExpect(jsonPath("$.size", is(3)))
				.andExpect(jsonPath("$.totalElements", is(8)))
				.andExpect(jsonPath("$.totalPages", is(3)));
	}
	
	@Test
	void shouldReturnBadRequestWhenItemConditionIsInvalid() throws Exception {
		mockMvc.perform(get(ITEM_FEED_URI)
				.param("itemCondition", "INVALID")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errorCode", 
						is(CommonErrorType.ILLEGAL_ENUMERATION.name())));
	}
	
	@Test
	void shouldReturnBadRequestWhenSortIsInvalid() throws Exception {
		mockMvc.perform(get(ITEM_FEED_URI)
				.param("sort", "PRICE_DES")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message", containsString("NEWEST")))
				.andExpect(jsonPath("$.message", containsString("PRICE_ASC")))
				.andExpect(jsonPath("$.message", containsString("PRICE_DESC")));
	}
}
