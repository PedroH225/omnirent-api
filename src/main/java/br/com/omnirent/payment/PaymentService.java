package br.com.omnirent.payment;

import java.lang.System.Logger;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.payment.dto.StripeCheckoutSession;
import br.com.omnirent.payment.enums.PaymentProvider;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.payment.model.Payment;
import br.com.omnirent.payment.stripe.StripeService;
import br.com.omnirent.rental.domain.Rental;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    
    private final StripeService stripeService;
    
    private final Clock clock;
    
    private final AppProperties appProperties;
    
    @Transactional
    public void createPayment(
            PaymentRequestedEvent event) {
    	String frontUrl = appProperties.frontUrl();
    	BigDecimal amount = event.amount();
    	
        Payment payment = Payment.create(event.rentalId(), amount, "brl");

        paymentRepository.save(payment);

        StripeCheckoutSession session =
                stripeService.createCheckoutSession(
                        amount.longValue() * 100,
                        "brl",
                        frontUrl + "/success",
                        frontUrl + "/cancel",
                        payment.getId()
                );

        payment.attachExternalReference(PaymentProvider.STRIPE, session.sessionId());
        
        paymentRepository.save(payment);
        
        log.debug("Session URL: {}", session.url());;
    }

    public void confirmPayment(String stripeSessionId) {
    	Payment payment = paymentRepository
                .findById(stripeSessionId)
                .orElseThrow();

        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }
        
        payment.markAsPaid(Instant.now(clock));
    }
}