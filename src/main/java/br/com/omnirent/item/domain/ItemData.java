package br.com.omnirent.item.domain;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.item.dto.ItemRequestDTO;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
public class ItemData {

	private String brand;

	private String model;

	private String description;

	private BigDecimal basePrice;
	
	@Enumerated(EnumType.STRING)
	private ItemCondition itemCondition;

	public ItemData(ItemRequestDTO itemDTO) {
	    this.brand = itemDTO.brand();
	    this.model = itemDTO.model();
	    this.description = itemDTO.description();
	    this.basePrice = itemDTO.basePrice();
	    this.itemCondition = ItemCondition.fromString(itemDTO.itemCondition());

	}

	public ItemData(String brand, String model, String description, BigDecimal basePrice, ItemCondition itemCondition) {
		this.brand = brand;
		this.model = model;
		this.description = description;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition;
	}
	
	
}
