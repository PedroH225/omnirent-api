package br.com.omnirent.payment.model;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.rental.domain.Rental;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "rental_id", nullable = false)
	private Rental rental;
	
	@Embedded
	private ExternalPaymentReference externalReference;;
	
	private BigDecimal amount;
	
	private String currency;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	private Instant paidAt;
	
//    public static Payment create() {
//        return;
//    }
}
