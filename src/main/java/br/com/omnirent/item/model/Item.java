package br.com.omnirent.item.model;

import java.math.BigDecimal;
import java.util.concurrent.locks.Condition;

import br.com.omnirent.common.model.NamedEntity;
import br.com.omnirent.item.enums.ItemCondition;
import br.com.omnirent.rental.enums.RentalStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
	
	@Enumerated(EnumType.STRING)
	private ItemCondition itemCondition;


}
