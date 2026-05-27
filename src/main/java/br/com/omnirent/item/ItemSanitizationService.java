package br.com.omnirent.item;

import org.springframework.stereotype.Component;

import br.com.omnirent.item.dto.ItemRequestDTO;

@Component
public class ItemSanitizationService {

	public ItemRequestDTO sanitizeItemFields(ItemRequestDTO itemDTO) {
		return new ItemRequestDTO(
				itemDTO.id(),
				sanitizeText(itemDTO.name()),
				sanitizeText(itemDTO.model()),
				sanitizeText(itemDTO.brand()),
				itemDTO.description().strip(),
				itemDTO.basePrice(),
				itemDTO.itemCondition(),
				sanitizeIdentifier(itemDTO.subCategoryId()),
				sanitizeIdentifier(itemDTO.addressId())
		);
	}
	
	private String sanitizeText(String text) {
		return text != null ?
				text.strip()
				.replaceAll("\\s+", " ")
				: null;
	}
	
	private String sanitizeIdentifier(String identifier) {
		return identifier != null
				? identifier.strip()
				: null;
	}
}
