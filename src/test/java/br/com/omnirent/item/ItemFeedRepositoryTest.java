package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import br.com.omnirent.config.CacheTestConfig;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.item.context.ItemFeedContext;
import jakarta.transaction.Transactional;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CacheTestConfig.class)
public class ItemFeedRepositoryTest extends IntegrationTest {
	
	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ItemQueryRepository queryRepository;
	
	@Test
	void shouldReturnAllItemsWhenNoFiltersApplied() {
        Pageable pageable = PageRequest.of(0, 20);
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(null, null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(15);
        assertThat(result.getTotalElements()).isEqualTo(15);
        assertThat(result.getTotalPages()).isEqualTo(1);
        assertThat(result.getNumber()).isZero();
    }
}
