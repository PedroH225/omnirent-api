package br.com.omnirent.payment.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.stripe.model.PaymentMethod;

import br.com.omnirent.common.BaseEntity;
import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.exception.domain.InvalidPaymentStateTransitionException;
import br.com.omnirent.exception.domain.PaymentReferenceAlreadyLockedException;
import br.com.omnirent.payment.enums.PaymentProvider;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.rental.domain.Rental;
import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name = "rental_id", insertable=false, updatable=false)
	private Rental rental;
	
	@Column(name = "rental_id")
	private String rentalId;
	
	@Embedded
	private ExternalPaymentReference externalReference;;
	
	private BigDecimal amount;
	
	private String currency;
	
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	private Instant paidAt;
	
	private void transitionTo(PaymentStatus target) {
	    if (!status.canTransition(target)) {
	        throw new InvalidPaymentStateTransitionException(status, target);
	    }

	    this.status = target;
	}
	
	public static Payment create(String rentalId, BigDecimal amount, String currency) {
	    Payment payment = new Payment();

	    payment.rentalId = rentalId;
	    payment.amount = amount;
	    payment.currency = currency;
	    payment.status = PaymentStatus.PENDING;
	    payment.paidAt = null;
	    
	    return payment;
	}
	
	public void attachExternalReference(
			PaymentProvider provider, String externalPaymentId) {
	    if (this.status != PaymentStatus.PENDING) {
	        throw new PaymentReferenceAlreadyLockedException();
	    }
	    
		this.externalReference = 
	    		new ExternalPaymentReference(provider, externalPaymentId, null);
	}
	
    public void markAsPaid(Instant paidAt) {
    	transitionTo(PaymentStatus.PAID);
        this.paidAt = paidAt;
    }
}
