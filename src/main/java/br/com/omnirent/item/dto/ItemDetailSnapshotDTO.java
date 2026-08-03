package br.com.omnirent.item.dto;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.domain.ItemSnapshot;
import lombok.Data;

@Data
public class ItemDetailSnapshotDTO {

	private String id;
	
	private String name;
	
	private String brand;
	
	private String model;
	
	private BigDecimal basePrice;
	
	private ItemCondition itemCondition;
	
	private String itemConditionLabel;
	
	private String description;
	
	private String thumbnailKey;
	
	public ItemDetailSnapshotDTO(String id, String name, String brand, String model, BigDecimal basePrice,
			ItemCondition itemCondition, String description, String thumbnailKey) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.model = model;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition;
		this.description = description;
		this.thumbnailKey = thumbnailKey;
	}
		
	public ItemDetailSnapshotDTO(ItemSnapshot item) {
		this.id = item.getId();
		this.name = item.getName();
		
		ItemData itemData = item.getItemData();
		
		this.brand = itemData.getBrand();
		this.model = itemData.getModel();
		this.description = itemData.getDescription();
		this.basePrice = itemData.getBasePrice();
		this.itemCondition = itemData.getItemCondition();
		this.thumbnailKey = item.getThumbnailKey();
	}
}
