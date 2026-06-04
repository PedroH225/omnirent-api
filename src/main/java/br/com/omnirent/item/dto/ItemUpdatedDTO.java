package br.com.omnirent.item.dto;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import lombok.Data;

@Data
public class ItemUpdatedDTO {
	
    private String id;
    
    private String name;
    
    private String brand;
    
    private String model;
    
    private String description;
    
    private BigDecimal basePrice;
    
    private ItemCondition itemCondition;
    
    private String itemConditionLabel;
    
    private ItemStatus itemStatus;
    
    private String itemStatusLabel;
    
    private String ownerId;
    
    private String addressId;
    
    private String subCategoryId;

    public ItemUpdatedDTO(
            String id, String name, String brand, String model, String description,
            BigDecimal basePrice, ItemCondition itemCondition, ItemStatus status, 
            String ownerId, String addressId, String subCategoryId) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.model = model;
        this.description = description;
        this.basePrice = basePrice;
        this.itemCondition = itemCondition;
        this.itemStatus = status;
        this.ownerId = ownerId;
        this.addressId = addressId;
        this.subCategoryId = subCategoryId;
    }
    
}
