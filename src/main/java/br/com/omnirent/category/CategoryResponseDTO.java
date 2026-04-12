package br.com.omnirent.category;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CategoryResponseDTO {
	
	private String id;
	
	private String name;
	
	private List<SubCategoryResDTO> subCategories = new ArrayList<SubCategoryResDTO>();

	public CategoryResponseDTO(String id, String name) {
		this.id = id;
		this.name = name;
	}
	
	
	
}
