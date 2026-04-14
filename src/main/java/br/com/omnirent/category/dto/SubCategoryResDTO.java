package br.com.omnirent.category.dto;

import br.com.omnirent.category.domain.SubCategory;
import lombok.Data;

@Data
public class SubCategoryResDTO {

	private String id;
	
	private String name;
	
	private String category;

	public SubCategoryResDTO(SubCategory subCategory) {
		this.id = subCategory.getId();
		this.name = subCategory.getName();
		
		this.category = subCategory.getCategory().getName();
	}

	public SubCategoryResDTO(String id, String name, String categoryName) {
		this.id = id;
		this.name = name;
		this.category = categoryName;
	}
	
	
}
