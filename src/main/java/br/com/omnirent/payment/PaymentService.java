package br.com.omnirent.payment;

import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.payment.model.Payment;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository repository;
    
    private final Clock clock;

    public void confirmPayment(String stripeSessionId) {
        Payment payment = repository
                .findByExternalReference_ExternalPaymentId(stripeSessionId)
                .orElseThrow();

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }
        
        payment.markAsPaid(Instant.now(clock));
    }
}