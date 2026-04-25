package br.com.omnirent.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class ItemCreatedDTO {
	private String id;
	
	private String name;
	
	private String brand;

	private String model;

	private String description;
	
	private BigDecimal basePrice;
	
	private String itemCondition;
	
	private String itemStatus;
			
	public ItemCreatedDTO(String id, String name, String brand, String model, String description,
			BigDecimal basePrice, ItemCondition itemCondition,
			ItemStatus itemStatus) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.model = model;
		this.description = description;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition.toString();
		this.itemStatus = itemStatus.toString();
	}
}
