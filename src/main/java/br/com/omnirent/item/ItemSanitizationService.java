package br.com.omnirent.item;

import org.springframework.stereotype.Component;

import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.item.dto.UpdateItemRequestDTO;

@Component
public class ItemSanitizationService {

	public ItemRequestDTO sanitizeItemFields(ItemRequestDTO itemDTO) {
		return new ItemRequestDTO(
				itemDTO.id(),
				sanitizeText(itemDTO.name()),
				sanitizeText(itemDTO.model()),
				sanitizeText(itemDTO.brand()),
				sanitizeDescription(itemDTO.description()),
				itemDTO.basePrice(),
				itemDTO.itemCondition(),
				sanitizeIdentifier(itemDTO.subCategoryId()),
				sanitizeIdentifier(itemDTO.addressId())
		);
	}
	
	public UpdateItemRequestDTO sanitizeUpdateItemFields(UpdateItemRequestDTO itemDTO) {
		return new UpdateItemRequestDTO(
				sanitizeIdentifier(itemDTO.id()),
				sanitizeText(itemDTO.name()),
				sanitizeText(itemDTO.model()),
				sanitizeText(itemDTO.brand()),
				sanitizeDescription(itemDTO.description()),
				itemDTO.basePrice(),
				itemDTO.itemCondition()
		);
	}
	
	private String sanitizeText(String text) {
		return text != null ?
				text.strip()
				.replaceAll("\\s+", " ")
				: null;
	}
	
	private String sanitizeDescription(String text) {
		return text != null
				? text.strip()
				: null;
	}
	
	private String sanitizeIdentifier(String identifier) {
		return identifier != null
				? identifier.strip()
				: null;
	}
}
