package br.com.omnirent.item;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.omnirent.address.AddressMapper;
import br.com.omnirent.address.AddressResponseDTO;
import br.com.omnirent.category.CategoryMapper;
import br.com.omnirent.category.SubCategoryResDTO;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.UserResponseDTO;
import lombok.Data;

@Data
public class ItemResponseDTO {
	
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

	public ItemResponseDTO(Item item) {
	    this.id = item.getId();
	    this.name = item.getName();
	    
	    ItemData itemData = item.getItemData();
	    
	    this.brand = itemData.getBrand();
	    this.model = itemData.getModel();
	    this.description = itemData.getDescription();
	    this.basePrice = itemData.getBasePrice();
	    this.itemCondition = itemData.getItemCondition().toString();
	    
	    this.itemStatus = item.getItemStatus().toString();
	    this.subCategory = CategoryMapper.toSubDto(item.getSubCategory());
	    this.pickupAddress = AddressMapper.toDto(item.getPickupAdress());
	    this.owner = UserMapper.toDto(item.getOwner());
	    
	    this.createdAt = dtf.format(item.getCreatedAt());
	    this.updatedAt = dtf.format(item.getUpdatedAt());
	}
}
