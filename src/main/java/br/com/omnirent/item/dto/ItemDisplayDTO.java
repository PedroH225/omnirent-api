package br.com.omnirent.item.dto;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import lombok.Data;

@Data
public class ItemDisplayDTO {
	
	private String id;
	
	private String name;
	
	private BigDecimal basePrice;
	
	private ItemCondition itemCondition;
	
	private String itemConditionLabel;
	
	private ItemStatus itemStatus;
	
	private String itemStatusLabel;
	
	private String subCategoryName;
	
	private String thumbnailKey;
			
	private Instant createdAt;

	public ItemDisplayDTO(String id, String name, BigDecimal basePrice, ItemCondition itemCondition,
			ItemStatus itemStatus, String subCategoryName, String thumbnailKey, Instant createdAt) {
		this.id = id;
		this.name = name;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition;
		this.itemStatus = itemStatus;
		this.subCategoryName = subCategoryName;
		this.thumbnailKey = thumbnailKey;
		this.createdAt = createdAt;
	}
}
