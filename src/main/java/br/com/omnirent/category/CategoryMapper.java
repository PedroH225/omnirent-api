package br.com.omnirent.category;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.SubCategoryResDTO;

@Component
public class CategoryMapper {

	public List<SubCategoryResDTO> toSubDto(List<SubCategory> subCategories) {
		return subCategories.stream()
				.map(sc -> toSubDto(sc))
				.collect(Collectors.toList());
	}
	
	public SubCategoryResDTO toSubDto(SubCategory subCategory) {
		return new SubCategoryResDTO(subCategory.getId(), subCategory.getName(),
				subCategory.getCategory().getName());
	}
}
