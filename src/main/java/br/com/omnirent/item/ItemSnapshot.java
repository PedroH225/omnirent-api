package br.com.omnirent.item;

import java.math.BigDecimal;

import br.com.omnirent.rental.Rental;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@Table(name = "item_snapshots")
@AllArgsConstructor
public class ItemSnapshot {	
	
	@Id
	private String rentalId;
	
	private String name;
	
	private String brand;
	
	private String model;
	
	private BigDecimal basePrice;
	
	private String itemCondition;
	
	private String subCategoryName;
	
	@MapsId
	@OneToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	public ItemSnapshot(Item item) {
		this.name = item.getName();
		
		ItemData itemData = item.getItemData();
		
		this.brand = itemData.getBrand();
		this.model = itemData.getModel();
		this.basePrice = itemData.getBasePrice();
		this.itemCondition = itemData.getItemCondition().toString();
		this.subCategoryName = item.getSubCategory().getName();
	}
	
}
