package br.com.omnirent.rental.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.omnirent.common.model.BaseEntity;
import br.com.omnirent.item.model.Item;
import br.com.omnirent.rental.enums.RentalPeriod;
import br.com.omnirent.rental.enums.RentalStatus;
import br.com.omnirent.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "rentals")
public class Rental extends BaseEntity {
	private static final long serialVersionUID = 1L;

	private LocalDateTime startDate;

	private LocalDateTime endDate;
	
	private BigDecimal finalPrice;
	
	@Enumerated(EnumType.STRING)
	private RentalStatus rentalStatus;
	
	@Enumerated(EnumType.STRING)
	private RentalPeriod rentalPeriod;
	
	@ManyToOne
	@JoinColumn(name = "item_id")
	private Item item;
	
	@ManyToOne
	@JoinColumn(name = "renter_id")
	private User renter;
	

}
