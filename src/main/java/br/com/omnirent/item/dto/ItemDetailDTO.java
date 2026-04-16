package br.com.omnirent.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class ItemDetailDTO {
	
	private String id;
	
	private String name;
	
	private String brand;

	private String model;

	private String description;
	
	private BigDecimal basePrice;
	
	private String itemCondition;
	
	private String itemStatus;
	
	private SubCategoryResDTO subCategory;
	
	private AddressResponseDTO pickupAddress;
	
	private UserResponseDTO owner;
	
	private String createdAt;
	
	private String updatedAt;
	
	@JsonIgnore
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public ItemDetailDTO(String id, String name, String brand, String model, String description,
			BigDecimal basePrice, ItemCondition itemCondition, ItemStatus itemStatus, SubCategoryResDTO subCategory,
			AddressResponseDTO pickupAddress, UserResponseDTO owner, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.id = id;
		this.name = name;
		this.brand = brand;
		this.model = model;
		this.description = description;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition.toString();
		this.itemStatus = itemStatus.toString();
		this.subCategory = subCategory;
		this.pickupAddress = pickupAddress;
		this.owner = owner;
		this.createdAt = dtf.format(createdAt);
		this.updatedAt = dtf.format(updatedAt);
	}

	

}
