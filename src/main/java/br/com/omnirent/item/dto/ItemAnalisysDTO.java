package br.com.omnirent.item.dto;

import java.math.BigDecimal;
import java.util.List;

import br.com.omnirent.address.dto.AddressSummaryDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.item.context.ItemImageResponseDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class ItemAnalisysDTO {
	
	private String id;
	
	private String name;
	
	private String brand;

	private String model;

	private String description;
	
	private BigDecimal basePrice;
		
	private ItemCondition itemCondition;
	
	private ItemStatus itemStatus;

	private SubCategoryResDTO subCategory;
	
	private AddressSummaryDTO pickupAddress;
	
	private UserResponseDTO owner;
	
	private List<ItemImageResponseDTO> images;

	public ItemAnalisysDTO(String id, String name, String brand, String model, String description, BigDecimal basePrice,
			ItemCondition itemCondition, ItemStatus itemStatus, SubCategoryResDTO subCategory,
			AddressSummaryDTO pickupAddress, UserResponseDTO owner) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.model = model;
		this.description = description;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition;
		this.itemStatus = itemStatus;
		this.subCategory = subCategory;
		this.pickupAddress = pickupAddress;
		this.owner = owner;
	}
}
