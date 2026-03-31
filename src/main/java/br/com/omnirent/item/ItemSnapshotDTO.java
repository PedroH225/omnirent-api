package br.com.omnirent.item;

import java.math.BigDecimal;

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
