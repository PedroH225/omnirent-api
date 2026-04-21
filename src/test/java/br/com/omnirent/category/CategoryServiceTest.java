package br.com.omnirent.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchReflectiveOperationException;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.CategoryResponseDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.exception.domain.CategoryNotFoundException;
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
    void shouldGetCategoryByIdWithSubCategories() {
    	String electronicsId = electronics.getId();
    	
    	Optional<CategoryResponseDTO> optCategory = Optional.of(CategoryTestFactory.toCategoryResDTO(electronics));
    	
    	SubCategoryResDTO dto1 = SubCategoryTestFactory.toSubDto(mouse);
    	SubCategoryResDTO dto2 = SubCategoryTestFactory.toSubDto(notebook);

    	List<SubCategoryResDTO> expected = List.of(dto1, dto2);
    	
    	when(categoryRepository.getCategoryById(electronicsId)).thenReturn(optCategory);
    	when(subRepository.findSubByCategoryId(electronicsId)).thenReturn(expected);
    	
    	CategoryResponseDTO result = categoryService.getCategoryById(electronicsId);
    	
    	assertThat(result).isNotNull();
    	assertThat(result.getId()).isEqualTo(electronicsId);
    	assertThat(result.getName()).isEqualTo(electronics.getName());
    	assertThat(result.getSubCategories()).isEqualTo(expected);
    	
    	verify(categoryRepository).getCategoryById(electronicsId);
    	verify(subRepository).findSubByCategoryId(electronicsId);
    	verifyNoMoreInteractions(subRepository, categoryRepository);
    }
    
    @Test
    void shouldThrowWhenCategoryNotFound() {
    	String invalidId = "invalid-id";
    	    
    	when(categoryRepository.getCategoryById(invalidId)).thenReturn(Optional.empty());
    	    	
    	assertThatThrownBy(() -> categoryService.getCategoryById(invalidId))
    	.isInstanceOf(CategoryNotFoundException.class);
    	
    	verify(categoryRepository).getCategoryById(invalidId);
    	verifyNoInteractions(subRepository);
    	verifyNoMoreInteractions(categoryRepository);
    }
    
    @Test
    void shouldGetSubCategoryById() {
    	String subCatId = notebook.getId();
    	
    	SubCategoryResDTO subCatDTO = SubCategoryTestFactory.toSubDto(notebook);
    	
    	when(subRepository.findById(subCatId)).thenReturn(Optional.of(notebook));
    	when(mapper.toSubDto(notebook)).thenReturn(subCatDTO);
    	
    	SubCategoryResDTO result = categoryService.getSubCategoryById(subCatId);
    	
    	assertThat(result).isNotNull();
    	assertThat(result.getId()).isEqualTo(subCatId);
    	assertThat(result.getName()).isEqualTo(notebook.getName());
    	
    	verify(subRepository).findById(subCatId);
    	verify(mapper).toSubDto(notebook);
    	verifyNoMoreInteractions(subRepository, mapper);
    }
}
