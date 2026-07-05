package br.com.omnirent.payment;

import java.math.BigDecimal;
import java.time.Clock;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.PaymentTestFactory;
import br.com.omnirent.factory.RentalTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.item.ItemRepository;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.payment.context.PaymentCanceledContext;
import br.com.omnirent.payment.context.PaymentConfirmedContext;
import br.com.omnirent.payment.context.PaymentExpiredContext;
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
import jakarta.transaction.Transactional;

@Transactional
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
    private SimpMessagingTemplate simpMessagingTemplate;

	@Mock
    private StripeService stripeService;

	@Autowired
    private Clock clock;

	@Autowired
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
    public void test() {
    	System.out.println("PaymentId: " +  payment.getId());
    }
    
}
