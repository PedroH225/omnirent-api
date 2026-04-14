package br.com.omnirent.category;

import java.util.List;
import java.util.stream.Collectors;

import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.SubCategoryResDTO;

public class CategoryMapper {

	public static List<SubCategoryResDTO> toSubDto(List<SubCategory> subCategories) {
		return subCategories.stream()
				.map(SubCategoryResDTO::new)
				.collect(Collectors.toList());
	}
	
	public static SubCategoryResDTO toSubDto(SubCategory subCategory) {
		return new SubCategoryResDTO(subCategory);
	}
}
