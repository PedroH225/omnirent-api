package br.com.omnirent.category;

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
}
