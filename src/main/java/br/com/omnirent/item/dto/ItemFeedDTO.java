package br.com.omnirent.item.dto;

import java.time.Instant;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemFeedDTO {
	private String id;
	
	private String name;
		
	private ItemCondition itemCondition;
	
	private String itemConditionLabel;
	
	private ItemPriceData price;
	
	private String subCategoryName;
	
	private Instant createdAt;
	
	private UserResponseDTO owner;
	
	private String thumbnailStorageKey;
}
