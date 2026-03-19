package br.com.omnirent.item;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ItemSnapshotDTO {

	private String rentalId;
	
	private String name;
	
	private String brand;
	
	private String model;
	
	private BigDecimal basePrice;
	
	private String itemCondition;
	
	private String subCategoryName;
	
	public ItemSnapshotDTO(ItemSnapshot item) {
		this.name = item.getName();
		this.brand = item.getBrand();
		this.model = item.getModel();
		this.basePrice = item.getBasePrice();
		this.itemCondition = item.getItemCondition();
		this.subCategoryName = item.getSubCategoryName();
	}
}
