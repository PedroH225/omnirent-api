package br.com.omnirent.item.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.Data;

@Data
public class ItemDisplayDTO {
	
	private String id;
	
	private String name;
	
	private BigDecimal basePrice;
	
	private ItemCondition itemCondition;
	
	private String itemConditionLabel;
	
	private ItemStatus itemStatus;
	
	private String itemStatusLabel;
	
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
		this.itemCondition = itemCondition;
		this.itemStatus = itemStatus;
		this.subCategoryName = subCategoryName;
		this.owner = owner;
		this.createdAt = dtf.format(createdAt);
	}
	
	
}
