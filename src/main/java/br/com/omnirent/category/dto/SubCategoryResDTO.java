package br.com.omnirent.category.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class SubCategoryResDTO {

	private String id;
	
	private String name;
	
	private String category;
	
	private String subCategoryLabel;
	
	private String categoryLabel;

	public SubCategoryResDTO(String id, String name, String categoryName) {
		this.id = id;
		this.name = name;
		this.category = categoryName;
	}
	
	@JsonIgnore
	public String getMessageKey() {
		return "subcategory." + this.name;  
	}
	
	@JsonIgnore
	public String getCategoryMessageKey() {
		return "category." + this.category;  
	}
}
