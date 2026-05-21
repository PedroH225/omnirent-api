package br.com.omnirent.category;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.category.dto.CategoryResponseDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.config.i18n.MessageService;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class CategoryMapper {
	
	private MessageService messageService;

	public List<SubCategoryResDTO> toSubDto(List<SubCategory> subCategories) {
		return subCategories.stream()
				.map(sc -> toSubDto(sc))
				.collect(Collectors.toList());
	}
	
	public SubCategoryResDTO toSubDto(SubCategory subCategory) {
		return new SubCategoryResDTO(subCategory.getId(), subCategory.getName(),
				subCategory.getCategory().getName());
	}
	
	public CategoryResponseDTO localize(CategoryResponseDTO categoryDTO) {
		String categoryLabel = messageService.get(categoryDTO.getMessageKey());
		categoryDTO.setCategoryLabel(categoryLabel);
		
		if (!categoryDTO.getSubCategories().isEmpty() 
				&& categoryDTO.getSubCategories() != null) {
			categoryDTO.getSubCategories().forEach(sc -> {
			sc.setSubCategoryLabel(messageService.get(sc.getMessageKey()));
			sc.setCategoryLabel(categoryLabel);
			});
		}
		
		return categoryDTO;
	}
	
	public SubCategoryResDTO localize(SubCategoryResDTO subCategoryDTO) {
		subCategoryDTO.setSubCategoryLabel(messageService.get(subCategoryDTO.getMessageKey()));
		subCategoryDTO.setCategoryLabel(messageService.get(subCategoryDTO.getCategoryMessageKey()));
		
		return subCategoryDTO;
	}
}
