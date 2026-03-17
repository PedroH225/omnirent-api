package br.com.omnirent.category;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CategoryService {

	private CategoryRepository categoryRepository;

	private SubCategoryRepository subRepository;
	
	public Category findById(String id) {
		Optional<Category> category = categoryRepository.findById(id);
		
		if (category.isEmpty()) {
			throw new RuntimeException("Category not found.");
		}
		
		return category.get();
	}
	
	public SubCategory findSubById(String id) {
		Optional<SubCategory> subCategory = subRepository.findById(id);
		
		if (subCategory.isEmpty()) {
			throw new RuntimeException("Sub Category not found.");
		}
		
		return subCategory.get();
	}
	
	public CategoryResponseDTO getCategoryById(String id) {
		Category category = findById(id);
		
		return CategoryMapper.toDto(category);
 	}
	
	public SubCategoryResDTO getSubCategoryById(String id) {
		SubCategory subCategory = findSubById(id);
		
		return CategoryMapper.toSubDto(subCategory);
 	}

	public List<CategoryResponseDTO> findAll() {
		return CategoryMapper.toDto(categoryRepository.findAll());
	}
	
	public List<SubCategoryResDTO> findAllSub() {
		return CategoryMapper.toSubDto(subRepository.findAll());
	}
	
}
