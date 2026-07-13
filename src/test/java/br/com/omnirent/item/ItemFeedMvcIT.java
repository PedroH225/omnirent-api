package br.com.omnirent.item;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

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
				.contentType(MediaType.APPLICATION_JSON))
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
}
