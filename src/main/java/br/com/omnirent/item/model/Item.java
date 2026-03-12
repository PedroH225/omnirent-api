package br.com.omnirent.item.model;

import java.math.BigDecimal;

import br.com.omnirent.common.model.NamedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "items")
public class Item extends NamedEntity {
	private static final long serialVersionUID = 1L;
	
	private String brand;

	private String model;

	private String description;

	private BigDecimal basePrice;


}
