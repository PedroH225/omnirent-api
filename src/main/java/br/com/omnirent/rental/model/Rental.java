package br.com.omnirent.rental.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.omnirent.common.model.BaseEntity;
import jakarta.persistence.Entity;
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

}
