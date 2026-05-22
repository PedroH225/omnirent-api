package br.com.omnirent.category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.CategoryResponseDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.CategoryErrorType;
import br.com.omnirent.exception.domain.SubCategoryErrorType;
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
			throw new ApiException(CategoryErrorType.NOT_FOUND);
		}
		
		CategoryResponseDTO category = optCategory.get();
		List<SubCategoryResDTO> subCategories = subRepository.findSubByCategoryId(id);
		category.setSubCategories(subCategories);
		
		return mapper.localize(category);
 	}
	
	public SubCategory getValidSubReference(String subCategoryId) {
		boolean found = subRepository.verifySubCategory(subCategoryId);
		if (!found) {
			throw new ApiException(SubCategoryErrorType.NOT_FOUND);
		}
		return subRepository.getReferenceById(subCategoryId);
	}

	
	public SubCategoryResDTO getSubCategoryById(String id) {		
		SubCategoryResDTO result = subRepository.findSubById(id)
				.orElseThrow(() -> new ApiException(SubCategoryErrorType.NOT_FOUND));
		
		return mapper.localize(result);
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
		
		categories.forEach(c -> mapper.localize(c));
		
		return categories;
	}
	
	public List<SubCategoryResDTO> findAllSub() {
		List<SubCategoryResDTO> result = subRepository.findAllSubCat();
		result.forEach(sc -> mapper.localize(sc));
		
		return result;
	}

	public List<SubCategoryResDTO> findSubsByCategory(String categoryName) {
		List<SubCategoryResDTO> result = subRepository.findAllSubByCategoryName(categoryName);
		result.forEach(sc -> mapper.localize(sc));
		
		return result;
	}
	
}
