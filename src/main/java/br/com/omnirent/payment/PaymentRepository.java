package br.com.omnirent.payment;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByExternalReference_ExternalPaymentId(String externalPaymentId);

    @Modifying
    @Query("""
    		UPDATE Payment p 
    		SET p.status = :targetStatus, p.externalReference.paymentIntent = :paymentIntent, p.paidAt = :paidAt
    		WHERE p.id = :id AND p.status = :pending
    		""")
	int confirmPayment(@Param("id")String paymentId, PaymentStatus pending, 
			String paymentIntent, PaymentStatus targetStatus, Instant paidAt);

    @Modifying
    @Query("""
    		UPDATE Payment p
    		SET p.status = :status
    		WHERE p.id = :paymentId AND p.status = :currStatus
    		""")
    int	updateStatus(String paymentId, PaymentStatus currStatus, PaymentStatus status);
    
    @Modifying
    @Query("""
    		UPDATE Payment p
    		SET p.status = :status
    		WHERE p.id = :paymentId
    		""")
    int updateStatus(String paymentId, PaymentStatus status);
}
