package br.com.omnirent.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.CategoryResponseDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.exception.domain.CategoryNotFoundException;
import br.com.omnirent.exception.domain.SubCategoryNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CategoryService {

	private CategoryRepository categoryRepository;

	private SubCategoryRepository subRepository;
	
	private CategoryMapper mapper;
	
	public Category findById(String id) {
		Optional<Category> category = categoryRepository.findById(id);
		
		if (category.isEmpty()) {
			throw new CategoryNotFoundException();
		}
		
		return category.get();
	}
	
	public SubCategory findSubById(String id) {
		Optional<SubCategory> subCategory = subRepository.findById(id);
		
		if (subCategory.isEmpty()) {
			throw new SubCategoryNotFoundException();
		}
		
		return subCategory.get();
	}
	
	public CategoryResponseDTO getCategoryById(String id) {
		Optional<CategoryResponseDTO> optCategory = categoryRepository.getCategoryById(id);
		
		List<SubCategoryResDTO> subCategories = subRepository.findSubByCategoryId(id);
		
		if (optCategory.isEmpty()) {
			throw new CategoryNotFoundException();
		}
		CategoryResponseDTO category = optCategory.get();
		category.setSubCategories(subCategories);
		
		return category;
 	}
	
	public SubCategoryResDTO getSubCategoryById(String id) {
		SubCategory subCategory = findSubById(id);
		
		return mapper.toSubDto(subCategory);
 	}

	public List<CategoryResponseDTO> findAll() {
		List<CategoryResponseDTO> categories = categoryRepository.getAllCategories();
		
		List<SubCategoryResDTO> subCategories = findAllSub();
		
		Map<String, List<SubCategoryResDTO>> groupByCat = new HashMap<String, List<SubCategoryResDTO>>();
		for (SubCategoryResDTO sub : subCategories) {
	        String categoryName = sub.getCategory();

	        groupByCat
	            .computeIfAbsent(categoryName, k -> new ArrayList<>())
	            .add(sub);
	    }
		
		for (CategoryResponseDTO cat : categories) {
	        List<SubCategoryResDTO> subs = groupByCat.get(cat.getName());

	        cat.setSubCategories(subs != null ? subs : new ArrayList<>());
	    }
		
		return categories;
	}
	
	public List<SubCategoryResDTO> findAllSub() {
		return subRepository.findAllSubCat();
	}

	public List<SubCategoryResDTO> findSubsByCategory(String categoryName) {
		List<SubCategory> subCategories = subRepository.findAllByCategoryName(categoryName);
		
		return mapper.toSubDto(subCategories);
	}
	
}
