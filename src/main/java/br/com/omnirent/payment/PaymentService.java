package br.com.omnirent.payment;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.domain.InvalidPaymentStateTransitionException;
import br.com.omnirent.exception.domain.InvalidRentalStatusTransitionException;
import br.com.omnirent.exception.domain.OptimisticLockException;
import br.com.omnirent.payment.dto.StripeCheckoutSession;
import br.com.omnirent.payment.enums.PaymentProvider;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.payment.model.Payment;
import br.com.omnirent.payment.stripe.StripeService;
import br.com.omnirent.rental.RentalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    
    private final PaymentQueryRepository queryRepository;
    
    private final RentalService rentalService;
    
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

    @Transactional
    public void confirmPayment(String paymentId) {
    	PaymentConfirmedContext context = queryRepository.
    			findConfirmedContext(paymentId)
    			.orElseThrow();

    	PaymentStatus currentStatus = context.status();
    	PaymentStatus targetStatus = PaymentStatus.PAID;
    	RentalStatus currentRentalStatus = context.rentalStatus();
    	
        if (currentStatus == targetStatus) {
            return;
        }
        
        if (!currentRentalStatus.canTransition(RentalStatus.CONFIRMED)) {
			throw new InvalidRentalStatusTransitionException(
					currentRentalStatus, currentRentalStatus);
		}
        validatePaymentTransition(currentStatus, targetStatus);
        
        Instant paidAt = Instant.now(clock);
        int updated = paymentRepository.confirmPayment
        		(paymentId, PaymentStatus.PENDING, targetStatus, paidAt);
        
        if (updated == 0) {
			throw new OptimisticLockException(
					PaymentConfirmedContext.class.getSimpleName(), paymentId);
		}
        
        rentalService.confirm(context.rentalId(), currentRentalStatus);
    }
    
	private void validatePaymentTransition(PaymentStatus currentStatus, PaymentStatus target) {
	    if (!currentStatus.canTransition(target)) {
	        throw new InvalidPaymentStateTransitionException(currentStatus, target);
	    }
	}
}