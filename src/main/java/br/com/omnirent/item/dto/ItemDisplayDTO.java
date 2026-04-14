package br.com.omnirent.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.category.dto.SubCategoryResDTO;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class ItemDisplayDTO {
	
	private String id;
	
	private String name;
	
	private BigDecimal basePrice;
	
	private String itemCondition;
	
	private String itemStatus;
	
	private String subCategoryName;
			
	private String createdAt;
	
	private UserResponseDTO owner;
		
	@JsonIgnore
	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

	public ItemDisplayDTO(String id, String name, BigDecimal basePrice, ItemCondition itemCondition,
			ItemStatus itemStatus, String subCategoryName, LocalDateTime createdAt, 
			UserResponseDTO owner) {
		this.id = id;
		this.name = name;
		this.basePrice = basePrice;
		this.itemCondition = itemCondition.toString();
		this.itemStatus = itemStatus.toString();
		this.subCategoryName = subCategoryName;
		this.owner = owner;
		this.createdAt = dtf.format(createdAt);
	}
	
	
}
