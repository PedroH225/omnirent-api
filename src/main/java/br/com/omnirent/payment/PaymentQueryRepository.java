package br.com.omnirent.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.omnirent.payment.context.PaymentCanceledContext;
import br.com.omnirent.payment.context.PaymentConfirmedContext;
import br.com.omnirent.payment.model.Payment;

public interface PaymentQueryRepository extends Repository<Payment, String> {

	@Query("""
			SELECT new br.com.omnirent.payment.context.PaymentConfirmedContext(
			p.id, p.status, r.id, r.rentalStatus)
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

	
}
