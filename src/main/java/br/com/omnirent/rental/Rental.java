package br.com.omnirent.rental;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.omnirent.common.BaseEntity;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.item.Item;
import br.com.omnirent.item.ItemSnapshot;
import br.com.omnirent.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
	@JoinColumn(name = "renter_id")
	private User renter;
	
	@OneToOne(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
	private ItemSnapshot itemSnapshot;
	

}
