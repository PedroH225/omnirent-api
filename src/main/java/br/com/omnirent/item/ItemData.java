package br.com.omnirent.item;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
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

	}
}
