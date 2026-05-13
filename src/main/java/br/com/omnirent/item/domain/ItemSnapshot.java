package br.com.omnirent.item.domain;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.rental.domain.Rental;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Table(name = "item_snapshots")
@NoArgsConstructor
public class ItemSnapshot {	
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	private String name;
	
	@Embedded
	private ItemData itemData;
	
	@ToString.Exclude
	@OneToOne
	@JoinColumn(name = "rental_id")
	private Rental rental;

	public ItemSnapshot(String name, String brand, String model,
		String description, BigDecimal basePrice, ItemCondition itemCondition) {
		this.name = name;

		this.itemData = new ItemData(brand, model, description, basePrice, itemCondition);
	}
	
}
