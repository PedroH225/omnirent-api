package br.com.omnirent.category;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/category")
public class CategoryController {

	private CategoryService categoryService;
	
	@GetMapping("/findAll")
	public List<CategoryResponseDTO> findAll() {
		return categoryService.findAll();
	}
	
	@GetMapping("/findAllSub")
	public List<SubCategoryResDTO> findAllSub() {
		return categoryService.findAllSub();
	}
	
	@GetMapping("/find/{id}")
	public CategoryResponseDTO findById(@PathVariable String id) {
		return categoryService.getCategoryById(id);
	}
	
	@GetMapping("/findSub/{id}")
	public SubCategoryResDTO findSubById(@PathVariable String id) {
		return categoryService.getSubCategoryById(id);
	}
	
}
