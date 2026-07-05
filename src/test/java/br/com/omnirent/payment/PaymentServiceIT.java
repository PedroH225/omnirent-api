package br.com.omnirent.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.domain.InvalidPaymentStateTransitionException;
import br.com.omnirent.exception.domain.PaymentNotFoundException;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.RentalTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.item.ItemRepository;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.payment.dto.CheckoutCompletedDTO;
import br.com.omnirent.payment.dto.StripeCheckoutSession;
import br.com.omnirent.payment.enums.PaymentProvider;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.payment.model.Payment;
import br.com.omnirent.payment.stripe.StripeService;
import br.com.omnirent.rental.RentalRepository;
import br.com.omnirent.rental.RentalService;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
public class PaymentServiceIT extends SpringIntegrationTest {

	@Autowired
    private PaymentService paymentService;

	@Autowired
    private RentalService rentalService;

	@Autowired
    private PaymentRepository paymentRepository;
	
	@Autowired
	private RentalRepository rentalRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private SubCategoryRepository subRepository;
	
	@Autowired
    private PaymentQueryRepository queryRepository;
	
	@Autowired
	private EntityManager entityManager;

	@MockitoBean
    private SimpMessagingTemplate simpMessagingTemplate;

	@MockitoBean
    private StripeService stripeService;

	@Autowired
    private Clock clock;
    
	private User owner;
	private User renter;

	private Address ownerAddress;
	private Address ownerAddress2;
	
	private Category tools;
	private SubCategory drill;

	private Item item;
	private Item item2;
	
	private Rental rental;
	private Rental rental2;
    
    private Payment payment;

    @BeforeEach
    void setUp() {
		owner = userRepository.save(UserTestFactory.owner());
		renter = userRepository.save(UserTestFactory.user());

		ownerAddress = addressRepository.save(AddressTestFactory.forUser(owner));
		ownerAddress2 = addressRepository.save(AddressTestFactory.forUser(owner));

        tools = categoryRepository.save(CategoryTestFactory.create("Tools"));
        drill = subRepository.save(SubCategoryTestFactory.create("Drill", tools));

        item = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, drill,
        		"200", ItemCondition.NEW));
        
        item2 = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, drill,
        		"100", ItemCondition.USED));
        
        rental = rentalRepository.save(RentalTestFactory.create(item, owner, renter, ownerAddress2, "4400", 
        		RentalStatus.CREATED, RentalPeriod.MONTHLY, null, null));
	
        rental2 = rentalRepository.save(RentalTestFactory.create(item2, owner, renter, ownerAddress2, "4400", 
        		RentalStatus.SHIPPED, RentalPeriod.MONTHLY, null, null));
                
        payment = Payment.create(rental.getId(), rental.getFinalPrice(), "brl");
        payment.attachExternalReference(PaymentProvider.STRIPE, "cs_test_123");
        payment = paymentRepository.save(payment);
        
		SecurityTestUtils.setAuthenticatedUser(owner.getId());
    }
    
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}
    
    @Test
    public void createPayment_ShouldCreatePaymentAndSendWebSocketMessage() {
        BigDecimal amount = new BigDecimal("150.00");
        PaymentRequestedEvent event = new PaymentRequestedEvent(rental2.getId(), renter.getId(), amount, "brl");
        
        StripeCheckoutSession mockSession = new StripeCheckoutSession("cs_test_mock123", "http://mock-url.com");
        when(stripeService.createCheckoutSession(anyLong(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(mockSession);

        paymentService.createPayment(event);

        verify(stripeService).createCheckoutSession(
                eq(15000L), eq("brl"), anyString(), anyString(), anyString()
        );

        ArgumentCaptor<CheckoutCompletedDTO> dtoCaptor = ArgumentCaptor.forClass(CheckoutCompletedDTO.class);
        verify(simpMessagingTemplate).convertAndSend(
                eq("/topic/rental/payment/" + rental2.getId()), 
                dtoCaptor.capture()
        );
        
        CheckoutCompletedDTO sentDto = dtoCaptor.getValue();
        assertEquals(rental2.getId(), sentDto.rentalId());
        assertEquals("http://mock-url.com", sentDto.checkoutUrl());
    }
    
    @Test
    public void confirmPayment_ShouldUpdateStatusToPaidAndConfirmRental() {
        String paymentIntent = "pi_test_123";
        paymentRepository.updateStatus(payment.getId(), PaymentStatus.PENDING);
        
        paymentService.confirmPayment(payment.getId(), paymentIntent);
        
        entityManager.flush();
        entityManager.clear();
        
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow();

        assertEquals(PaymentStatus.PAID, updatedPayment.getStatus());
        assertEquals(paymentIntent, updatedPayment.getExternalReference().getPaymentIntent());

        Rental updatedRental = rentalRepository.findById(rental.getId()).orElseThrow();
        assertEquals(RentalStatus.CONFIRMED, updatedRental.getRentalStatus());
    }
    
    @Test
    public void confirmPayment_ShouldThrowExceptionWhenPaymentNotFound() {
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.confirmPayment("nonexistent-id", "pi_test_123");
        });
    }
    
    @Test
    public void confirmPayment_ShouldThrowExceptionOnInvalidTransition() {
        paymentRepository.updateStatus(payment.getId(), PaymentStatus.CANCELLED);

        assertThrows(InvalidPaymentStateTransitionException.class, () -> {
            paymentService.confirmPayment(payment.getId(), "pi_test_123");
        });
    }
    
}
