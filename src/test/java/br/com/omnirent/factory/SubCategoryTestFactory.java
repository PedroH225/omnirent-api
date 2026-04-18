package br.com.omnirent.factory;

import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;

public final class SubCategoryTestFactory {
	
    private SubCategoryTestFactory() {}

    public static SubCategory create(String name, Category category) {
        SubCategory subCategory = new SubCategory();
        subCategory.setName(name);
        subCategory.setCategory(category);
        return subCategory;
    }
}
