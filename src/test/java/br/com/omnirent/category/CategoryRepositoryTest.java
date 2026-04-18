package br.com.omnirent.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.CategoryResponseDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.config.CacheTestConfig;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.integration.IntegrationTest;
import jakarta.transaction.Transactional;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CacheTestConfig.class)
public class CategoryRepositoryTest extends IntegrationTest {
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private SubCategoryRepository subRepository;
	
	private Category electronics;
    private Category sports;
	
	private SubCategory notebook;
    private SubCategory mouse;
    private SubCategory ball;

    @BeforeAll
    void setUp() {
        electronics = new Category();
        electronics.setName("Electronics");
        electronics = categoryRepository.save(electronics);

        sports = new Category();
        sports.setName("Sports");
        sports = categoryRepository.save(sports);

        notebook = new SubCategory();
        notebook.setName("PC Gamer");
        notebook.setCategory(electronics);
        notebook = subRepository.save(notebook);

        mouse = new SubCategory();
        mouse.setName("Mouse");
        mouse.setCategory(electronics);
        mouse = subRepository.save(mouse);

        ball = new SubCategory();
        ball.setName("Ball");
        ball.setCategory(sports);
        ball = subRepository.save(ball);
    }

	@Test
	void shouldFindCategoryById() {
		Optional<CategoryResponseDTO> findCategory = categoryRepository.getCategoryById(electronics.getId());
		
		assertThat(findCategory).isPresent();
		assertThat(findCategory.get())
		.satisfies(c -> {
			assertThat(c.getId()).isEqualTo(electronics.getId());
			assertThat(c.getName()).isEqualTo(electronics.getName());
		});
	}
	
	@Test
	void shouldFindAllCategory() {
		List<CategoryResponseDTO> findCategories = categoryRepository.getAllCategories();

		assertThat(findCategories).isNotEmpty();
	}
	
	@Test
	void shouldFindSubCategoryById() {
		List<SubCategoryResDTO> findSubCategory = subRepository.findSubByCategoryId(electronics.getId());
		
		assertThat(findSubCategory).isNotEmpty();
		assertThat(findSubCategory.get(0).getCategory()).isEqualTo(electronics.getName());
	}
	
	@Test
	void shouldFindAllSubCategories() {
		List<SubCategoryResDTO> findSubCategory = subRepository.findAllSubCat();
		
		assertThat(findSubCategory).isNotEmpty();
	}
}
