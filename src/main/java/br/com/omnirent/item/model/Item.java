package br.com.omnirent.item.model;

import java.math.BigDecimal;
import java.util.List;

import br.com.omnirent.adress.model.Address;
import br.com.omnirent.category.model.SubCategory;
import br.com.omnirent.common.model.NamedEntity;
import br.com.omnirent.item.enums.ItemCondition;
import br.com.omnirent.rental.model.Rental;
import br.com.omnirent.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
	
	@ManyToOne
	@JoinColumn(name = "sub_category_id")
	private SubCategory subCategory;
	
	@ManyToOne
	@JoinColumn(name = "pickup_address_id")
	private Address pickupAdress;
	
	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User owner;
	
	@OneToMany(mappedBy = "item")
	private List<Rental> rentals;
}
