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
	
	public CategoryResponseDTO getCategoryById(String id) {
		Optional<CategoryResponseDTO> optCategory = categoryRepository.getCategoryById(id);
				
		if (optCategory.isEmpty()) {
			throw new CategoryNotFoundException();
		}
		
		CategoryResponseDTO category = optCategory.get();
		List<SubCategoryResDTO> subCategories = subRepository.findSubByCategoryId(id);
		category.setSubCategories(subCategories);
		
		return category;
 	}
	
	public SubCategory getValidSubReference(String subCategoryId) {
		boolean found = subRepository.verifySubCategory(subCategoryId);
		if (!found) {
			throw new SubCategoryNotFoundException();
		}
		return subRepository.getReferenceById(subCategoryId);
	}

	
	public SubCategoryResDTO getSubCategoryById(String id) {		
		return subRepository.findSubById(id)
				.orElseThrow(SubCategoryNotFoundException::new);
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
