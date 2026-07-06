package br.com.omnirent.payment;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.payment.context.PaymentCanceledContext;
import br.com.omnirent.payment.context.PaymentConfirmedContext;
import br.com.omnirent.payment.context.PaymentExpiredContext;
import br.com.omnirent.payment.context.PaymentRefundContext;
import br.com.omnirent.payment.context.ReopenPaymentContext;
import br.com.omnirent.payment.model.Payment;

public interface PaymentQueryRepository extends Repository<Payment, String> {

	@Query("""
			SELECT new br.com.omnirent.payment.context.PaymentConfirmedContext(
			p.id, p.status, r.id, r.rentalStatus, p.externalReference.paymentIntent,
			p.paidAt)
			FROM Payment p JOIN p.rental r
			WHERE p.id = :id
			""")
	Optional<PaymentConfirmedContext> findConfirmedContext(@Param("id")String paymentId);

	@Query("""
			SELECT new br.com.omnirent.payment.context.PaymentCanceledContext(
			p.id, p.status, p.externalReference.paymentIntent)
			FROM Payment p JOIN p.rental r
			WHERE r.id = :rentalId 
			""")
	Optional<PaymentCanceledContext> findCanceledContext(String rentalId);

	@Query("""
			SELECT new br.com.omnirent.payment.context.PaymentRefundContext(p.status)
			FROM Payment p
			WHERE p.id = :id
			""")
	Optional<PaymentRefundContext> findRefundContext(String id);
	
	@Query("""
			SELECT p.id FROM Payment p
			WHERE p.status = :pending AND p.createdAt <= :threshold
			""")
	List<String> findExpiredIds(
			PaymentStatus pending, Instant threshold);
	
	@Query("""
			SELECT new br.com.omnirent.payment.context.PaymentExpiredContext(
			p.id, p.externalReference.externalPaymentId, p.status, p.rentalId)
			FROM Payment p 
			WHERE p.id = :id
			""")
	Optional<PaymentExpiredContext> findExpiredPayment(@Param("id")String paymentId); 

	@Query("""
			SELECT new br.com.omnirent.payment.context.ReopenPaymentContext(
			p.id, r.id, r.finalPrice, p.status)
			FROM Payment p JOIN p.rental r
			WHERE r.id = :rentalId
			""")
	Optional<ReopenPaymentContext> findRopenPaymentContext(String rentalId);
}
