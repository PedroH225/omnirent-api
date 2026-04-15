package br.com.omnirent.item.dto;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.domain.ItemSnapshot;
import lombok.Data;

@Data
public class ItemSnapshotDTO {

	private String id;
	
	private String name;
	
	private String brand;
	
	private String model;
	
	private BigDecimal basePrice;
	
	private String itemCondition;
	
	private String description;
	
	public ItemSnapshotDTO(String id, String name, String brand, String model, BigDecimal basePrice,
			ItemCondition itemCondition, String description) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.model = model;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition.toString();
		this.description = description;
	}
		
	public ItemSnapshotDTO(ItemSnapshot item) {
		this.id = item.getId();
		this.name = item.getName();
		
		ItemData itemData = item.getItemData();
		
		this.brand = itemData.getBrand();
		this.model = itemData.getModel();
		this.description = itemData.getDescription();
		this.basePrice = itemData.getBasePrice();
		this.itemCondition = itemData.getItemCondition().toString();
	}
}
