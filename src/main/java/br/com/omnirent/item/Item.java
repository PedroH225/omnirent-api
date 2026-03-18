package br.com.omnirent.item;

import java.math.BigDecimal;
import java.util.List;

import br.com.omnirent.address.Address;
import br.com.omnirent.category.SubCategory;
import br.com.omnirent.common.NamedEntity;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.rental.Rental;
import br.com.omnirent.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
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
	
	@Enumerated(EnumType.STRING)
	private ItemStatus itemStatus;
	
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
	
	public void updateItemStatus(String status) {
	    this.itemStatus = ItemStatus.fromString(status);
	}
}
