package br.com.omnirent.factory;

import java.util.List;
import java.util.stream.Collectors;

import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.utils.Sequence;

public final class SubCategoryTestFactory {

	private SubCategoryTestFactory() {
	}

	public static SubCategory create(String name, Category category) {
		SubCategory subCategory = new SubCategory();
		subCategory.setName(name);
		subCategory.setCategory(category);
		return subCategory;
	}

	public static SubCategory createPersisted(String name, Category category) {
		SubCategory subCategory = create(name, category);
		subCategory.setId(Sequence.nextString("subCategoryId"));
		return subCategory;
	}

	public static SubCategoryResDTO toSubDto(SubCategory subCategory) {
		return new SubCategoryResDTO(subCategory.getId(), subCategory.getName(), subCategory.getCategory().getName());
	}

	public static List<SubCategoryResDTO> toSubDto(List<SubCategory> subCategories) {
		return subCategories.stream().map(s -> new SubCategoryResDTO(s.getId(), s.getName(), s.getCategory().getName()))
				.collect(Collectors.toList());
	}

}
