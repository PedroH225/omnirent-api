package br.com.omnirent.payment;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.domain.InvalidPaymentStateTransitionException;
import br.com.omnirent.exception.domain.InvalidRentalStatusTransitionException;
import br.com.omnirent.exception.domain.OptimisticLockException;
import br.com.omnirent.exception.domain.PaymentNotFoundException;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.PaymentTestFactory;
import br.com.omnirent.factory.RentalTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.payment.context.PaymentCanceledContext;
import br.com.omnirent.payment.context.PaymentConfirmedContext;
import br.com.omnirent.payment.context.PaymentExpiredContext;
import br.com.omnirent.payment.context.PaymentRefundContext;
import br.com.omnirent.payment.dto.CheckoutCompletedDTO;
import br.com.omnirent.payment.dto.StripeCheckoutSession;
import br.com.omnirent.payment.enums.PaymentProvider;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.payment.model.Payment;
import br.com.omnirent.payment.stripe.StripeService;
import br.com.omnirent.rental.RentalService;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.domain.User;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    
    @Mock
    private RentalService rentalService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentQueryRepository queryRepository;

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private StripeService stripeService;

    @Mock
    private Clock clock;

    @Mock
    private AppProperties appProperties;
    
    @Mock
    private SpringDomainEventPublisher eventPublisher;
    
    @Mock
    private PaymentMapper mapper;
    
	private User owner;
	private User renter;

	private Address ownerAddress;
	private Address ownerAddress2;
	
	private Category tools;
	private SubCategory drill;

	private Item item;
	private Item item2;
	
	private Rental rental;
    
    private Payment payment;
    
    private String paymentId;
    private String paymentIntent;
    private String sessionId;
    
    private PaymentRequestedEvent paymentRequestedEvent;
    private StripeCheckoutSession stripeSession;
    
    private PaymentConfirmedContext confirmedContext;
    private PaymentCanceledContext canceledContext;
    private PaymentExpiredContext expiredContext;
    private PaymentRefundContext refundContext;
    
    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    @Captor
    private ArgumentCaptor<CheckoutCompletedDTO> dtoCaptor;

    @BeforeEach
    void setUp() {
    	paymentId = "pay-456";
        paymentIntent = "pi_789";
        sessionId = "cs_test_123";
		owner = UserTestFactory.persistedOwner();
		renter = UserTestFactory.persistedUser();

		ownerAddress = AddressTestFactory.forPersistedUser(owner);
		ownerAddress2 = AddressTestFactory.forPersistedUser(owner);

        tools = CategoryTestFactory.createPersisted("Tools");
        drill = SubCategoryTestFactory.createPersisted("Drill", tools);

        item = ItemTestFactory.createPersisted(owner, ownerAddress, drill,
        		"200", ItemCondition.NEW);
        
        item2 = ItemTestFactory.createPersisted(owner, ownerAddress, drill,
        		"100", ItemCondition.USED);
        
        rental = RentalTestFactory.createPersisted(item, owner, renter, ownerAddress2, "4400", 
        		RentalStatus.CREATED, RentalPeriod.MONTHLY, null, null);
        
        paymentRequestedEvent = new PaymentRequestedEvent(rental.getId(), renter.getId(), new BigDecimal("150.00"), "brl");
        
        stripeSession = new StripeCheckoutSession(sessionId, "https://checkout.stripe.com/pay/cs_test_123");

        confirmedContext = PaymentTestFactory.createConfirmedContext(
                paymentId, PaymentStatus.PENDING, rental.getId(), RentalStatus.CREATED, null, Instant.now(clock));

        canceledContext = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PENDING, paymentIntent);
                
        expiredContext = PaymentTestFactory.createExpiredContext(
                paymentId, sessionId, PaymentStatus.PENDING, rental.getId());
        
        refundContext = PaymentTestFactory.createRefundContext(PaymentStatus.REFUND_REQUESTED);
    }

    @Test
    void createPayment_ShouldSavePaymentAndCreateStripeSession() {
        String frontUrl = "https://omnirent.com.br";
        when(appProperties.frontUrl()).thenReturn(frontUrl);
        when(stripeService.createCheckoutSession(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenReturn(stripeSession);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mock(Payment.class));
        
        paymentService.createPayment(paymentRequestedEvent);

        verify(paymentRepository, times(2)).save(any(Payment.class));
        verify(stripeService).createCheckoutSession(
                15000L,
                "brl",
                frontUrl + "/success",
                frontUrl + "/cancel",
                null
        );
        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/rental/payment/" + rental.getId()),
                any(CheckoutCompletedDTO.class)
        );
    }

    @Test
    void confirmPayment_ShouldConfirmPaymentAndUpdateRental_WhenValid() {
        Instant fixedInstant = Instant.parse("2026-07-04T10:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);
        
        when(queryRepository.findConfirmedContext(paymentId)).thenReturn(Optional.of(confirmedContext));
        when(paymentRepository.confirmPayment(
                eq(paymentId), eq(PaymentStatus.PENDING), eq(paymentIntent), eq(PaymentStatus.PAID), any(Instant.class)))
                .thenReturn(1);

        paymentService.confirmPayment(paymentId, paymentIntent);

        verify(paymentRepository).confirmPayment(paymentId, PaymentStatus.PENDING, paymentIntent, PaymentStatus.PAID, fixedInstant);
        verify(rentalService).confirm(rental.getId(), RentalStatus.CREATED);
    }

    @Test
    void confirmPayment_ShouldThrowOptimisticLockException_WhenUpdateFails() {
        Instant fixedInstant = Instant.parse("2026-07-04T10:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);

        when(queryRepository.findConfirmedContext(paymentId)).thenReturn(Optional.of(confirmedContext));
        when(paymentRepository.confirmPayment(anyString(), any(), anyString(), any(), any(Instant.class)))
                .thenReturn(0);

        assertThrows(OptimisticLockException.class, () -> 
            paymentService.confirmPayment(paymentId, paymentIntent)
        );
        verify(rentalService, never()).confirm(anyString(), any());
    }

    @Test
    void confirmPayment_ShouldThrowPaymentNotFoundException_WhenContextNotFound() {
        when(queryRepository.findConfirmedContext(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> 
            paymentService.confirmPayment(paymentId, paymentIntent)
        );
        verify(paymentRepository, never()).confirmPayment(anyString(), any(), anyString(), any(), any());
    }

    @Test
    void cancelPayment_ShouldUpdateStatusToCanceled() {
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.of(canceledContext));
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.CANCELLED))
                .thenReturn(1);

        paymentService.cancelPayment(rental.getId(), rental.getRenterId());

        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.CANCELLED);
    }

    @Test
    void requestRefund_ShouldCallStripeAndUpateStatusToRefundRequested() {
        PaymentCanceledContext paidContext = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PAID, paymentIntent);
                
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.of(paidContext));
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.PAID, PaymentStatus.REFUND_REQUESTED))
                .thenReturn(1);

        paymentService.requestRefund(rental.getId(), rental.getRenterId());

        verify(stripeService).requestRefund(paymentId, paymentIntent);
        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.PAID, PaymentStatus.REFUND_REQUESTED);
    }

    @Test
    void refundPayment_ShouldUpdateStatusToRefunded() {
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.REFUND_REQUESTED, PaymentStatus.REFUNDED)).thenReturn(1);
        when(queryRepository.findRefundContext(paymentId)).thenReturn(Optional.of(refundContext));
        
        paymentService.refundPayment(paymentId);

        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.REFUND_REQUESTED, PaymentStatus.REFUNDED);
    }

    @Test
    void expirePayment_ShouldExpireStripeSessionAndUpdateRentalAndPayment() {
        when(queryRepository.findExpiredPayment(paymentId)).thenReturn(Optional.of(expiredContext));
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.EXPIRED))
                .thenReturn(1);

        paymentService.expirePayment(paymentId);

        verify(stripeService).expirePayment(sessionId);
        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.EXPIRED);
        verify(rentalService).expire(rental.getId());
    }
    
    @Test
    void createPayment_ShouldThrowException_WhenStripeFails() {
        when(appProperties.frontUrl()).thenReturn("http://localhost");
        when(stripeService.createCheckoutSession(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenThrow(new RuntimeException("Stripe API Error"));

        assertThrows(RuntimeException.class, () -> paymentService.createPayment(paymentRequestedEvent));

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(simpMessagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void createPayment_ShouldThrowException_WhenRepositoryFails() {
        when(paymentRepository.save(any(Payment.class))).thenThrow(new DataIntegrityViolationException("DB Error"));

        assertThrows(DataIntegrityViolationException.class, () -> paymentService.createPayment(paymentRequestedEvent));

        verify(stripeService, never()).createCheckoutSession(anyLong(), anyString(), anyString(), anyString(), any());
        verify(simpMessagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void createPayment_ShouldAttachStripeProviderAndSessionId() {
        when(appProperties.frontUrl()).thenReturn("http://localhost");
        StripeCheckoutSession session = new StripeCheckoutSession(sessionId, "http://checkout.url");
        when(stripeService.createCheckoutSession(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenReturn(session);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mock(Payment.class));
       
        paymentService.createPayment(paymentRequestedEvent);

        verify(paymentRepository, times(2)).save(paymentCaptor.capture());
        Payment savedPayment = paymentCaptor.getValue();

        assertEquals(PaymentProvider.STRIPE, savedPayment.getExternalReference().getPaymentProvider());
        assertEquals(sessionId, savedPayment.getExternalReference().getExternalPaymentId());
    }

    @Test
    void createPayment_ShouldSendCorrectCheckoutCompletedDTO() {
        when(appProperties.frontUrl()).thenReturn("http://localhost");
        String checkoutUrl = "http://checkout.url";
        StripeCheckoutSession session = new StripeCheckoutSession(sessionId, checkoutUrl);
        when(stripeService.createCheckoutSession(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenReturn(session);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mock(Payment.class));

        paymentService.createPayment(paymentRequestedEvent);

        verify(simpMessagingTemplate).convertAndSend(eq("/topic/rental/payment/" + rental.getId()), dtoCaptor.capture());
        CheckoutCompletedDTO sentDto = dtoCaptor.getValue();

        assertEquals(rental.getId(), sentDto.rentalId());
        assertEquals(checkoutUrl, sentDto.checkoutUrl());
        assertEquals("CHECKOUT_CREATED", sentDto.status());
    }

    @Test
    void confirmPayment_ShouldReturnImmediately_WhenAlreadyPaid() {
        PaymentConfirmedContext context = PaymentTestFactory.createConfirmedContext(
                paymentId, PaymentStatus.PAID, rental.getId(), RentalStatus.CONFIRMED,
                "intent-id", Instant.now(clock));
        when(queryRepository.findConfirmedContext(paymentId)).thenReturn(Optional.of(context));

        paymentService.confirmPayment(paymentId, paymentIntent);

        verify(paymentRepository, never()).confirmPayment(anyString(), any(), anyString(), any(), any());
        verify(rentalService, never()).confirm(anyString(), any());
    }

    @Test
    void confirmPayment_ShouldThrowException_WhenInvalidPaymentTransition() {
        PaymentConfirmedContext context = PaymentTestFactory.createConfirmedContext(
                paymentId, PaymentStatus.CANCELLED, rental.getId(), RentalStatus.CREATED,
                "intent-id", Instant.now(clock));
        when(queryRepository.findConfirmedContext(paymentId)).thenReturn(Optional.of(context));

        assertThrows(InvalidPaymentStateTransitionException.class, 
                () -> paymentService.confirmPayment(paymentId, paymentIntent));

        verify(paymentRepository, never()).confirmPayment(anyString(), any(), anyString(), any(), any());
        verify(rentalService, never()).confirm(anyString(), any());
    }

    @Test
    void confirmPayment_ShouldThrowException_WhenInvalidRentalTransition() {
        PaymentConfirmedContext context = PaymentTestFactory.createConfirmedContext(
                paymentId, PaymentStatus.PENDING, rental.getId(), RentalStatus.CANCELLED,
                "intent-id", Instant.now(clock));
        when(queryRepository.findConfirmedContext(paymentId)).thenReturn(Optional.of(context));

        assertThrows(InvalidRentalStatusTransitionException.class, 
                () -> paymentService.confirmPayment(paymentId, paymentIntent));

        verify(paymentRepository, never()).confirmPayment(anyString(), any(), anyString(), any(), any());
        verify(rentalService, never()).confirm(anyString(), any());
    }

    @Test
    void confirmPayment_ShouldPropagateException_WhenRentalServiceFails() {
        Instant fixedInstant = Instant.parse("2026-07-04T10:00:00Z");
        when(clock.instant()).thenReturn(fixedInstant);

        PaymentConfirmedContext context = PaymentTestFactory.createConfirmedContext(
                paymentId, PaymentStatus.PENDING, rental.getId(), RentalStatus.CREATED,
                "intent-id", Instant.now(clock));
        when(queryRepository.findConfirmedContext(paymentId)).thenReturn(Optional.of(context));
        when(paymentRepository.confirmPayment(anyString(), any(), anyString(), any(), any(Instant.class)))
                .thenReturn(1);
        doThrow(new RuntimeException("Rental confirm failed")).when(rentalService).confirm(rental.getRenterId(), RentalStatus. CREATED);

        assertThrows(RuntimeException.class, () -> paymentService.confirmPayment(paymentId, paymentIntent));

        verify(paymentRepository).confirmPayment(paymentId, PaymentStatus.PENDING, paymentIntent, PaymentStatus.PAID, fixedInstant);
    }

    @Test
    void cancelPayment_ShouldThrowException_WhenPaymentNotFound() {
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.cancelPayment(rental.getId(), rental.getRenterId()));

        verify(paymentRepository, never()).updateStatus(anyString(), any(), any());
    }

    @Test
    void cancelPayment_ShouldThrowException_WhenInvalidTransition() {
        PaymentCanceledContext context = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PAID, paymentIntent);
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.of(context));

        assertThrows(InvalidPaymentStateTransitionException.class, () -> paymentService.cancelPayment(rental.getId(), rental.getRenterId()));

        verify(paymentRepository, never()).updateStatus(anyString(), any(), any());
    }

    @Test
    void cancelPayment_ShouldThrowOptimisticLockException_WhenUpdateFails() {
        PaymentCanceledContext context = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PENDING, paymentIntent);
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.of(context));
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.CANCELLED)).thenReturn(0);

        assertThrows(OptimisticLockException.class, () -> paymentService.cancelPayment(rental.getId(), rental.getRenterId()));
    }

    @Test
    void requestRefund_ShouldThrowException_WhenPaymentNotFound() {
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.requestRefund(rental.getId(), rental.getRenterId()));

        verify(stripeService, never()).requestRefund(anyString(), anyString());
        verify(paymentRepository, never()).updateStatus(anyString(), any(), any());
    }

    @Test
    void requestRefund_ShouldThrowException_WhenInvalidTransition() {
        PaymentCanceledContext context = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PENDING, paymentIntent);
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.of(context));

        assertThrows(InvalidPaymentStateTransitionException.class, () -> paymentService.requestRefund(rental.getId(), rental.getRenterId()));

        verify(stripeService, never()).requestRefund(anyString(), anyString());
        verify(paymentRepository, never()).updateStatus(anyString(), any(), any());
    }

    @Test
    void requestRefund_ShouldNotUpdateRepository_WhenStripeFails() {
        PaymentCanceledContext context = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PAID, paymentIntent);
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.of(context));
        doThrow(new RuntimeException("Stripe error")).when(stripeService).requestRefund(paymentId, paymentIntent);

        assertThrows(RuntimeException.class, () -> paymentService.requestRefund(rental.getId(), rental.getRenterId()));

        verify(paymentRepository, never()).updateStatus(anyString(), any(), any());
    }

    @Test
    void requestRefund_ShouldThrowOptimisticLockException_WhenUpdateFails() {
        PaymentCanceledContext context = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PAID, paymentIntent);
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.of(context));
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.PAID, PaymentStatus.REFUND_REQUESTED)).thenReturn(0);

        assertThrows(OptimisticLockException.class, () -> paymentService.requestRefund(rental.getId(), rental.getRenterId()));

        verify(stripeService).requestRefund(paymentId, paymentIntent);
    }

    @Test
    void refundPayment_ShouldThrowOptimisticLockException_WhenUpdateFails() {
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.REFUND_REQUESTED, PaymentStatus.REFUNDED)).thenReturn(0);
        when(queryRepository.findRefundContext(paymentId)).thenReturn(Optional.of(refundContext));
        
        assertThrows(OptimisticLockException.class, () -> paymentService.refundPayment(paymentId));
    }

    @Test
    void expirePayment_ShouldThrowException_WhenPaymentNotFound() {
        when(queryRepository.findExpiredPayment(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () -> paymentService.expirePayment(paymentId));

        verify(stripeService, never()).expirePayment(anyString());
        verify(paymentRepository, never()).updateStatus(anyString(), any(), any());
        verify(rentalService, never()).expire(anyString());
    }

    @Test
    void expirePayment_ShouldThrowException_WhenInvalidTransition() {
        PaymentExpiredContext context = PaymentTestFactory.createExpiredContext(
                paymentId, sessionId, PaymentStatus.PAID, rental.getId());
        when(queryRepository.findExpiredPayment(paymentId)).thenReturn(Optional.of(context));

        assertThrows(InvalidPaymentStateTransitionException.class, () -> paymentService.expirePayment(paymentId));

        verify(stripeService, never()).expirePayment(anyString());
        verify(paymentRepository, never()).updateStatus(anyString(), any(), any());
        verify(rentalService, never()).expire(anyString());
    }

    @Test
    void expirePayment_ShouldNotCallDownstream_WhenStripeFails() {
        PaymentExpiredContext context = PaymentTestFactory.createExpiredContext(
                paymentId, sessionId, PaymentStatus.PENDING, rental.getId());
        when(queryRepository.findExpiredPayment(paymentId)).thenReturn(Optional.of(context));
        doThrow(new RuntimeException("Stripe error")).when(stripeService).expirePayment(sessionId);

        assertThrows(RuntimeException.class, () -> paymentService.expirePayment(paymentId));

        verify(paymentRepository, never()).updateStatus(anyString(), any(), any());
        verify(rentalService, never()).expire(anyString());
    }

    @Test
    void expirePayment_ShouldThrowOptimisticLockException_WhenUpdateFails() {
        PaymentExpiredContext context = PaymentTestFactory.createExpiredContext(
                paymentId, sessionId, PaymentStatus.PENDING, rental.getId());
        when(queryRepository.findExpiredPayment(paymentId)).thenReturn(Optional.of(context));
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.EXPIRED)).thenReturn(0);

        assertThrows(OptimisticLockException.class, () -> paymentService.expirePayment(paymentId));

        verify(rentalService, never()).expire(anyString());
    }

    @Test
    void expirePayment_ShouldPropagateException_WhenRentalServiceFails() {
        PaymentExpiredContext context = PaymentTestFactory.createExpiredContext(
                paymentId, sessionId, PaymentStatus.PENDING, rental.getId());
        when(queryRepository.findExpiredPayment(paymentId)).thenReturn(Optional.of(context));
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.EXPIRED)).thenReturn(1);
        doThrow(new RuntimeException("Rental expire failed")).when(rentalService).expire(rental.getRenterId());

        assertThrows(RuntimeException.class, () -> paymentService.expirePayment(paymentId));

        verify(stripeService).expirePayment(sessionId);
        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.EXPIRED);
    }
}