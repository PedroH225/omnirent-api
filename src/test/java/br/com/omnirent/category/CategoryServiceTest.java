package br.com.omnirent.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

	@InjectMocks
	private CategoryService categoryService;
	
	@Mock
	private CategoryRepository categoryRepository;

	@Mock
	private SubCategoryRepository subRepository;
	
	@Mock
	private CategoryMapper mapper;
	
	private Category electronics;
    private Category sports;
	
	private SubCategory notebook;
    private SubCategory mouse;
    private SubCategory ball;

    @BeforeEach
    void setUp() {
        electronics = CategoryTestFactory.createPersisted("Eletronics");

        sports = CategoryTestFactory.createPersisted("Sports");

        notebook = SubCategoryTestFactory.createPersisted("Notebook", electronics);
        mouse = SubCategoryTestFactory.createPersisted("Mouse", electronics);
        ball = SubCategoryTestFactory.createPersisted("Ball", electronics);
    }
    
    @Test
    void test() {
    	System.out.println("ball id: " + ball.getId());
    	System.out.println("eletronicsid: " +  electronics.getId());
    	System.out.println("categoryservicetest working");
    }
}
