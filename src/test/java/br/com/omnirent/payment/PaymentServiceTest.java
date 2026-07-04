package br.com.omnirent.payment;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.config.properties.AppProperties;
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
import br.com.omnirent.payment.dto.CheckoutCompletedDTO;
import br.com.omnirent.payment.dto.StripeCheckoutSession;
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
                paymentId, PaymentStatus.PENDING, rental.getId(), RentalStatus.CREATED);

        canceledContext = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PENDING, paymentIntent);
                
        expiredContext = PaymentTestFactory.createExpiredContext(
                paymentId, sessionId, PaymentStatus.PENDING, rental.getId());
    }

    @Test
    void createPayment_ShouldSavePaymentAndCreateStripeSession() {
        String frontUrl = "https://omnirent.com.br";
        when(appProperties.frontUrl()).thenReturn(frontUrl);
        when(stripeService.createCheckoutSession(anyLong(), anyString(), anyString(), anyString(), any()))
                .thenReturn(stripeSession);

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

        paymentService.cancelPayment(rental.getId());

        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.PENDING, PaymentStatus.CANCELLED);
    }

    @Test
    void requestRefund_ShouldCallStripeAndUpateStatusToRefundRequested() {
        PaymentCanceledContext paidContext = PaymentTestFactory.createCanceledContext(
                paymentId, PaymentStatus.PAID, paymentIntent);
                
        when(queryRepository.findCanceledContext(rental.getId())).thenReturn(Optional.of(paidContext));
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.PAID, PaymentStatus.REFUND_REQUESTED))
                .thenReturn(1);

        paymentService.requestRefund(rental.getId());

        verify(stripeService).requestRefund(paymentId, paymentIntent);
        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.PAID, PaymentStatus.REFUND_REQUESTED);
    }

    @Test
    void refundPayment_ShouldUpdateStatusToRefunded() {
        when(paymentRepository.updateStatus(paymentId, PaymentStatus.REFUNDED)).thenReturn(1);

        paymentService.refundPayment(paymentId);

        verify(paymentRepository).updateStatus(paymentId, PaymentStatus.REFUNDED);
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
}