package br.com.omnirent.category;

import java.util.List;

import lombok.Data;

@Data
public class CategoryResponseDTO {
	
	private String id;
	
	private String name;
	
	private List<SubCategoryResDTO> subCategories;

	public CategoryResponseDTO(Category category) {
		this.id = category.getId();
		this.name = category.getName();
		
		this.subCategories = CategoryMapper.toSubDto(category.getSubCategories());
	}
	
	
	
}
