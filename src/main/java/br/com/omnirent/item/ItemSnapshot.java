package br.com.omnirent.item;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.rental.Rental;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "item_snapshots")
@NoArgsConstructor
public class ItemSnapshot {	
	
	@Id
	private String rentalId;
	
	private String name;
	
	private ItemData itemData;
	
	@MapsId("rentalId")
	@OneToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	public ItemSnapshot(Item item) {
		this.name = item.getName();
		
		this.itemData = item.getItemData();
	}
	
}
