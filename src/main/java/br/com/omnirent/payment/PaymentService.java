package br.com.omnirent.payment;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.audit.AuditEntry;
import br.com.omnirent.common.audit.AuditService;
import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.InvalidPaymentStateTransitionException;
import br.com.omnirent.exception.domain.InvalidRentalStatusTransitionException;
import br.com.omnirent.exception.domain.OptimisticLockException;
import br.com.omnirent.exception.domain.PaymentNotFoundException;
import br.com.omnirent.exception.domain.apptype.PaymentErrorType;
import br.com.omnirent.payment.context.PaymentCanceledContext;
import br.com.omnirent.payment.context.PaymentConfirmedContext;
import br.com.omnirent.payment.context.PaymentExpiredContext;
import br.com.omnirent.payment.context.PaymentRefundContext;
import br.com.omnirent.payment.context.ReopenPaymentContext;
import br.com.omnirent.payment.context.audit.PaymentAuditSnapshot;
import br.com.omnirent.payment.context.audit.PaymentConfirmedAuditSnapshot;
import br.com.omnirent.payment.context.audit.PaymentStatusChangedAuditSnapshot;
import br.com.omnirent.payment.dto.CheckoutCompletedDTO;
import br.com.omnirent.payment.dto.PaymentConfirmedResponse;
import br.com.omnirent.payment.dto.PaymentCreatedResponse;
import br.com.omnirent.payment.dto.PaymentHistoryEntryResponse;
import br.com.omnirent.payment.dto.PaymentHistoryResponse;
import br.com.omnirent.payment.dto.PaymentStatusChangeResponse;
import br.com.omnirent.payment.dto.StripeCheckoutSession;
import br.com.omnirent.payment.enums.PaymentProvider;
import br.com.omnirent.payment.event.PaymentConfirmedEvent;
import br.com.omnirent.payment.event.PaymentCreatedEvent;
import br.com.omnirent.payment.event.PaymentReinitializedEvent;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.payment.event.PaymentStatusChangedEvent;
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
    
    private final SimpMessagingTemplate simpMessagingTemplate;
    
    private final RentalService rentalService;
    
    private final StripeService stripeService;
    
    private final Clock clock;
    
    private final AppProperties appProperties;
    
    private final SpringDomainEventPublisher eventPublisher;
    
    private final PaymentMapper mapper;
    
    private final AuditService auditService;
    
    @Transactional
    public void createPayment(
            PaymentRequestedEvent event) {
    	BigDecimal amount = event.amount();
    	
        Payment payment = Payment.create(event.rentalId(), amount, "brl");

        paymentRepository.save(payment);

        StripeCheckoutSession session = createCheckoutSession(amount, "brl", payment.getId());

        payment.attachExternalReference(PaymentProvider.STRIPE, session.sessionId(), session.url());
        
        payment = paymentRepository.save(payment);

        simpMessagingTemplate.convertAndSend(
        		"/topic/rental/payment/" + event.rentalId(), 
        		new CheckoutCompletedDTO(
        				event.rentalId(), session.url(), "CHECKOUT_CREATED"));
        
        log.debug("Session URL: {}", session.url());;
        
        eventPublisher.publish(new PaymentCreatedEvent(
        		AuditAction.PAYMENT_CREATED, event.userId(), payment.getId(),
        		mapper.toAuditSnapshot(payment), Instant.now(clock)));
    }

    @Transactional
    public void confirmPayment(String paymentId, String paymentIntent) {
    	PaymentConfirmedContext context = queryRepository.
    			findConfirmedContext(paymentId)
    			.orElseThrow(() -> new PaymentNotFoundException(paymentId));

    	PaymentStatus currentStatus = context.status();
    	PaymentStatus targetStatus = PaymentStatus.PAID;
    	RentalStatus currentRentalStatus = context.rentalStatus();
    	 
    	if (currentStatus == targetStatus) {
    	    return;
    	}
    	
        validateRentalStatus(currentRentalStatus);
        validatePaymentTransition(currentStatus, targetStatus);

        Instant paidAt = Instant.now(clock);
        int updated = paymentRepository.confirmPayment
        		(paymentId, PaymentStatus.PENDING, paymentIntent, targetStatus, paidAt);

        if (updated == 0) {
			throw new OptimisticLockException(
					PaymentConfirmedContext.class.getSimpleName(), paymentId);
		}
        
        if (currentRentalStatus.equals(RentalStatus.LATE)) {
			rentalService.renewRental(context.rentalId());
		} else if (currentRentalStatus.equals(RentalStatus.CREATED)) {
	        rentalService.confirm(context.rentalId(), currentRentalStatus);
		}
        
        eventPublisher.publish(new PaymentConfirmedEvent(
        		AuditAction.PAYMENT_CONFIRMED, "SYSTEM_WEBHOOK", paymentId,
        		mapper.toConfirmedSnapshot(context.rentalId(), targetStatus, paymentIntent, paidAt), 
        		mapper.toConfirmedSnapshot(context), 
        		Instant.now(clock)));
    }
    
    @Transactional
	public void cancelPayment(String rentalId, String actorId) {
		PaymentStatus targetStatus = PaymentStatus.CANCELLED;
		PaymentCanceledContext context = queryRepository.findCanceledContext(rentalId)
				.orElseThrow(() -> new PaymentNotFoundException(
						"rentalId: " + rentalId)); 
		
		PaymentStatus currStatus = context.paymentStatus();
		validatePaymentTransition(context.paymentStatus(), targetStatus);
		
		String paymentId = context.paymentId();
		int updated = paymentRepository.updateStatus(paymentId, currStatus, targetStatus);
        
		if (updated == 0) {
			throw new OptimisticLockException(
					PaymentCanceledContext.class.getSimpleName(), paymentId);
		}
		
		publishDefaultStatusChangedEvent(
				actorId, paymentId, targetStatus, currStatus);
	}
	
    @Transactional
	public void requestRefund(String rentalId, String actorId) {
		PaymentStatus targetStatus = PaymentStatus.REFUND_REQUESTED;
		PaymentCanceledContext context = queryRepository.findCanceledContext(rentalId)
				.orElseThrow(() -> new PaymentNotFoundException(
						"rentalId: " + rentalId)); 
		
		PaymentStatus currStatus = context.paymentStatus();
		validatePaymentTransition(context.paymentStatus(), targetStatus);
		
		String paymentId = context.paymentId();
		stripeService.requestRefund(paymentId, context.paymentIntent());
		
		int updated = paymentRepository.updateStatus(paymentId, currStatus, targetStatus);

	    if (updated == 0) {
			throw new OptimisticLockException(
					"PaymentRefundRequested", paymentId);
	    }
	    
		publishDefaultStatusChangedEvent(
				actorId, paymentId, targetStatus, currStatus);
	}
    
    @Transactional
	public void refundPayment(String paymentId) {
		PaymentRefundContext context = queryRepository.findRefundContext(paymentId)
				.orElseThrow(() -> new PaymentNotFoundException(paymentId)); 
		
		PaymentStatus currStatus = context.currentStatus();
		PaymentStatus targetStatus = PaymentStatus.REFUNDED;

		int updated = paymentRepository.updateStatus(
				paymentId, currStatus, targetStatus);

	    if (updated == 0) {
			throw new OptimisticLockException(
					"PaymentRefunded", paymentId);
	    }
	    
		publishDefaultStatusChangedEvent(
				"SYSTEM_WEBHOOK", paymentId, targetStatus, currStatus);
	}
    
    @Transactional
	public void expirePayment(String paymentId) {
		PaymentStatus targetStatus = PaymentStatus.EXPIRED;
		PaymentExpiredContext context = queryRepository.findExpiredPayment(paymentId)
				.orElseThrow(() -> new PaymentNotFoundException(paymentId));
		
		PaymentStatus currStatus = context.status();
		validatePaymentTransition(currStatus, targetStatus);
		
		stripeService.expirePayment(context.sessionId());
		
		int updated = paymentRepository.updateStatus(paymentId, currStatus, targetStatus);
		
	    if (updated == 0) {
			throw new OptimisticLockException(
					PaymentExpiredContext.class.getSimpleName(), paymentId);
	    }
	    rentalService.expire(context.rentalId());
	    
	    publishDefaultStatusChangedEvent(
				"SYSTEM_SCHEDULER", paymentId, targetStatus, currStatus);
	}
	

	@Transactional
	public void restartPaymentFlow(String rentalId) {
		ReopenPaymentContext context = queryRepository.findRopenPaymentContext(rentalId)
				.orElseThrow(() -> new PaymentNotFoundException("rentalId: " + rentalId));
		
		PaymentStatus targetStatus = PaymentStatus.PENDING;
		String currency = "brl";
		
		StripeCheckoutSession session = createCheckoutSession(
				context.finalPrice(), currency, context.paymentId());
		
		paymentRepository.reinitializePayment(context.paymentId(), context.currentPaymentStatus(),
				session.sessionId(), PaymentProvider.STRIPE, targetStatus,
				context.finalPrice(), currency);
		
        log.debug("Session URL: {}", session.url());
        
        eventPublisher.publish(new PaymentReinitializedEvent(
        		AuditAction.PAYMENT_REINITIALIZED, "SYSTEM_SCHEDULER", context.paymentId(),
        		mapper.toAuditSnapshot(
        				context, targetStatus, currency, PaymentProvider.STRIPE,
        				session.sessionId(), null),
        		mapper.toAuditSnapshot(context),
        		Instant.now(clock)));
	}
	
	private void publishDefaultStatusChangedEvent(String actorId, String paymentId,
			PaymentStatus newStatus, PaymentStatus oldStatus) {
		eventPublisher.publish(new PaymentStatusChangedEvent(
				AuditAction.PAYMENT_STATUS_CHANGED,
				actorId, paymentId, 
				new PaymentStatusChangedAuditSnapshot(newStatus),
				new PaymentStatusChangedAuditSnapshot(oldStatus),
				Instant.now(clock)));
	}
	
	public List<PaymentHistoryResponse> getPaymentHistory(String rentalId) {
		String paymentId = queryRepository.getPaymentId(rentalId)
				.orElseThrow(() -> new ApiException(PaymentErrorType.NOT_FOUND));
		
		List<AuditEntry> auditEntries = auditService.getAuditLog(paymentId);
		
		return auditEntries.stream()
				.map(a -> new PaymentHistoryResponse(
						a.action(), a.actorId(), 
						resolveDto(a.currentBody(), a.action()), 
						resolveDto(a.previousBody(), a.action()),
						a.occurredAt()))
				.collect(Collectors.toList());
	}
	
	private PaymentHistoryEntryResponse resolveDto(AuditBody body, AuditAction action) {
		if (body == null) {
		    return null;
		}
		
		return switch (action) {
        case PAYMENT_CREATED -> {
            PaymentAuditSnapshot snapshot = (PaymentAuditSnapshot) body;
            yield mapper.toPaymentResponse(snapshot);
        } case PAYMENT_REINITIALIZED -> {
            PaymentAuditSnapshot snapshot = (PaymentAuditSnapshot) body;
        	yield mapper.toPaymentResponse(snapshot);
        } case PAYMENT_STATUS_CHANGED -> {
            PaymentStatusChangedAuditSnapshot snapshot = (PaymentStatusChangedAuditSnapshot) body;
            yield new PaymentStatusChangeResponse(snapshot.status());
        } case PAYMENT_CONFIRMED -> {
            PaymentConfirmedAuditSnapshot snapshot = (PaymentConfirmedAuditSnapshot) body;
            yield new PaymentConfirmedResponse(snapshot.status(), snapshot.paidAt());
        }
		default -> throw new IllegalArgumentException("Unexpected value: " + action);
    };
		
	}
	
	private StripeCheckoutSession createCheckoutSession(
			BigDecimal amount, String currency, String paymentId) {
        String frontUrl = appProperties.frontUrl();
		return stripeService.createCheckoutSession(
                        amount.longValue() * 100,
                        currency,
                        frontUrl + "/success",
                        frontUrl + "/cancel",
                        paymentId);
	}
	
	private void validatePaymentTransition(PaymentStatus currentStatus, PaymentStatus target) {
	    if (!currentStatus.canTransition(target)) {
	        throw new InvalidPaymentStateTransitionException(currentStatus, target);
	    }
	}
	
	private void validateRentalStatus(RentalStatus currentRentalStatus) { 
		Map<RentalStatus, RentalStatus> transitions = Map.of(
			    RentalStatus.CREATED, RentalStatus.CONFIRMED,
			    RentalStatus.LATE, RentalStatus.IN_USE
			);

			RentalStatus target = transitions.get(currentRentalStatus);

			if (target == null) {
			    throw new InvalidRentalStatusTransitionException(currentRentalStatus, null);
			}
	}
}