package br.com.omnirent.factory;

import br.com.omnirent.category.domain.Category;

public final class CategoryTestFactory {

    private CategoryTestFactory() {}

    public static Category create(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }
}
