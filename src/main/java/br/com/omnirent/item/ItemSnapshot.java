package br.com.omnirent.item;

import java.math.BigDecimal;

import br.com.omnirent.rental.Rental;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "item_snapshots")
public class ItemSnapshot {	
	
	@EmbeddedId
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
}
