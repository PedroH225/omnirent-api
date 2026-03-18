package br.com.omnirent.category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryMapper {

	public static List<SubCategoryResDTO> toSubDto(List<SubCategory> subCategories) {
		return subCategories.stream()
				.map(SubCategoryResDTO::new)
				.collect(Collectors.toList());
	}
	
	public static List<CategoryResponseDTO> toDto(List<Category> categories) {
		return categories.stream()
				.map(CategoryResponseDTO::new)
				.collect(Collectors.toList());
	}
	
	public static CategoryResponseDTO toDto(Category category) {
		return new CategoryResponseDTO(category);
	}
	
	public static SubCategoryResDTO toSubDto(SubCategory subCategory) {
		return new SubCategoryResDTO(subCategory);
	}
}
