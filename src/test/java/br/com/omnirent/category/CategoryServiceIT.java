package br.com.omnirent.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import jakarta.transaction.Transactional;

@Transactional
public class CategoryServiceIT extends SpringIntegrationTest {
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private SubCategoryRepository subRepository;
	
	private Category electronics;
    private Category sports;
	
	private SubCategory notebook;
    private SubCategory mouse;
    private SubCategory ball;

    @BeforeEach
    void setUp() {
        electronics = categoryRepository.save(CategoryTestFactory.create("Eletronics"));

        sports = categoryRepository.save(CategoryTestFactory.create("Sports"));

        notebook = subRepository.save(SubCategoryTestFactory.create("Notebook", electronics));
        mouse = subRepository.save(SubCategoryTestFactory.create("Mouse", electronics));
        ball = subRepository.save(SubCategoryTestFactory.create("Ball", electronics));
    }
    
    @Test
    void test() {
    	System.out.println(electronics.getId());
    	System.out.println("categoryServiceIT working");
    }
}
