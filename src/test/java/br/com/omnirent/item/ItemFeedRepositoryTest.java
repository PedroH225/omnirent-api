package br.com.omnirent.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Comparator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.omnirent.common.enums.ItemCondition;
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
    private ItemQueryRepository queryRepository;

    @Test
    void shouldReturnAllItemsWhenNoFiltersApplied() {
        Pageable pageable = PageRequest.of(0, 20);
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(null, null, null, null, pageable);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getTotalElements()).isEqualTo(8);
        assertThat(result.getTotalPages()).isEqualTo(1);
        }

    @Test
    void shouldFilterByName() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts("canon", null, null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent())
                .extracting(ItemFeedContext::name)
                .allMatch(name -> name.toLowerCase().contains("canon"));
    }

    @Test
    void shouldFilterByCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(null, "IT", null, null, pageable);

        assertThat(result.getContent())
	        .extracting(ItemFeedContext::subCategoryName)
	        .containsOnly("LAPTOP");
    }

    @Test
    void shouldFilterBySubCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(null, null, "CAMERA", null, pageable);

        assertThat(result.getContent())
	        .extracting(ItemFeedContext::subCategoryName)
	        .containsOnly("CAMERA");
    }

    @Test
    void shouldFilterByItemCondition() {
        Pageable pageable = PageRequest.of(0, 20);
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(null, null, null, ItemCondition.GOOD, pageable);

        assertThat(result.getContent()).hasSize(4);
    }

    @Test
    void shouldFilterByCombinationOfFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(
                "Canon", 
                "AUDIOVISUAL", 
                "CAMERA", 
                ItemCondition.GOOD, 
                pageable
        );

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).name()).isEqualTo("Canon T6 Camera");
    }

    @Test
    void shouldReturnEmptyWhenNoResultsMatch() {
        Pageable pageable = PageRequest.of(0, 10);
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts("Playstation 5", null, null, null, pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void shouldSortByCreatedAtDesc() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(null, null, null, null, pageable);

        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent().get(0).name()).isEqualTo("Dell XPS 15");
    }

    @Test
    void shouldSortByBasePriceAsc() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "itemData.basePrice"));
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(null, null, null, null, pageable);

        assertThat(result.getContent()).isNotEmpty();
        
        assertThat(result.getContent())
	        .extracting(ItemFeedContext::basePrice)
	        .isSorted();
    }

    @Test
    void shouldSortByBasePriceDesc() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "itemData.basePrice"));
        
        Page<ItemFeedContext> result = queryRepository.getFeedContexts(null, null, null, null, pageable);

        assertThat(result.getContent()).isNotEmpty();
        
        assertThat(result.getContent())
	        .extracting(ItemFeedContext::basePrice)
	        .isSortedAccordingTo(Comparator.reverseOrder());
    }

    @Test
    void shouldPaginateResults() {
        int pageSize = 3;
        
        Page<ItemFeedContext> page0 = queryRepository.getFeedContexts(null, null, null, null, PageRequest.of(0, pageSize));
        assertThat(page0.getContent()).hasSize(3);
        
        Page<ItemFeedContext> page1 = queryRepository.getFeedContexts(null, null, null, null, PageRequest.of(1, pageSize));
        assertThat(page1.getContent()).hasSize(3);

        assertThat(page0.getContent()).doesNotContainAnyElementsOf(page1.getContent());
        
        assertThat(page0.getTotalElements()).isEqualTo(8);
        assertThat(page1.getTotalElements()).isEqualTo(8);
    }
}

