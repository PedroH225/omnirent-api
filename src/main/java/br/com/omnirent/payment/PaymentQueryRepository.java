package br.com.omnirent.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.omnirent.payment.model.Payment;

public interface PaymentQueryRepository extends Repository<Payment, String> {

	@Query("""
			SELECT new br.com.omnirent.payment.PaymentConfirmedContext(
			p.id, p.status, r.id, r.rentalStatus)
			FROM Payment p JOIN p.rental r
			WHERE p.id = :id
			""")
	Optional<PaymentConfirmedContext> findConfirmedContext(@Param("id")String paymentId);

	
}
