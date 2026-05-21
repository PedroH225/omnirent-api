package br.com.omnirent.item.domain;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
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

	public ItemData(String brand, String model, String description, BigDecimal basePrice, ItemCondition itemCondition) {
		this.brand = brand;
		this.model = model;
		this.description = description;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition;
	}
}
