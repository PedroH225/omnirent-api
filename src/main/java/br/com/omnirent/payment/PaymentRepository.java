package br.com.omnirent.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.omnirent.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByExternalReference_ExternalPaymentId(String externalPaymentId);

}
