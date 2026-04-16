package br.com.omnirent.rental.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.omnirent.address.domain.AddressSnapshot;
import br.com.omnirent.common.BaseEntity;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.domain.IllegalRentalStateException;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
	
	@Column(name = "renter_id")
	private String renterId;
	
	@Column(name = "owner_id")
	private String ownerId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "renter_id", insertable = false, updatable = false)
	private User renter;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", insertable = false, updatable = false)
	private User owner;
	
	@OneToOne(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
	private ItemSnapshot itemSnapshot;
	
	@OneToOne(mappedBy = "rental", cascade = CascadeType.ALL, orphanRemoval = true)
	private AddressSnapshot addressSnapshot;
	
	public void assignOwner(User owner) {
		this.owner = owner;
		this.ownerId = owner.getId();
	}
	
	public void assignRenter(User renter, String renterId) {
		this.renter = renter;
		this.renterId = renterId;
	}
}
