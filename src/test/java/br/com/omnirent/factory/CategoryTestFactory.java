package br.com.omnirent.factory;

import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.dto.CategoryResponseDTO;
import br.com.omnirent.utils.Sequence;

public final class CategoryTestFactory {

    private CategoryTestFactory() {}

    public static Category create(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }
    
    public static Category createPersisted(String name) {
    	Category category = create(name);
    	category.setId(Sequence.nextString("categoryId"));
    	return category;
    }
    
    public static CategoryResponseDTO toCategoryResDTO(Category category) {
    	return new CategoryResponseDTO(category.getId(), category.getName());
    }
}
