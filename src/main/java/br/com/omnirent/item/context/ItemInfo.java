package br.com.omnirent.item.context;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import lombok.Data;

@Data
public class ItemInfo {
	private String id;
	
	private String itemName;
	
	private String brand;

	private String model;

	private String description;

	private BigDecimal basePrice;
	
	private ItemCondition itemCondition;

	public ItemInfo(String id, String itemName, String brand, String model, String description,
			BigDecimal basePrice, ItemCondition itemCondition) {
		this.id = id;
		this.itemName = itemName;
		this.brand = brand;
		this.model = model;
		this.description = description;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition;
	}
	
	
}
