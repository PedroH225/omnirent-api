package br.com.omnirent.rental;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.NativeDetector.Context;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.domain.UserNotFoundException;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.RentalTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.item.ItemService;
import br.com.omnirent.item.context.ItemInfo;
import br.com.omnirent.item.context.ItemRentedContext;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.dto.ItemRequestDTO;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.domain.RentalAuthorizationService;
import br.com.omnirent.rental.domain.RentalPriceService;
import br.com.omnirent.rental.dto.RentalCreatedDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.rental.dto.RentalRequestDTO;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {
	
	@InjectMocks
	private RentalService rentalService;
	
	@Mock
	private RentalRepository rentalRepository;
	
	@Mock
	private RentalQueryRepository queryRepository;
	
	@Mock
	private ItemService itemService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private RentalAuthorizationService authorizationService;
	
	@Mock
	private RentalMapper mapper;
	
	@Mock
	private CurrentUserProvider currentUserProvider;
	
	private User owner;
	private User renter;

	private Address ownerAddress;
	private Address ownerAddress2;
	
	private Category tools;
	private SubCategory drill;

	private Item item;
	private Item item2;
	
	private Rental rental;
	
	@BeforeEach
	void setUp() {
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
	}
	
	@Test
	void shouldFindUserRentedItems() {
		String renterId = renter.getId();
		
		List<RentalDisplayDTO> expected = List.of(RentalTestFactory.toRentalDisplayDTO(rental));
		
		when(currentUserProvider.currentUserId()).thenReturn(renterId);
		when(queryRepository.findUserRented(renterId)).thenReturn(expected);
		
		List<RentalDisplayDTO> result = rentalService.findUserRented();
		
		assertThat(result).isEqualTo(expected);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(renterId);
	}
	
	@Test
	void shouldThrowWhenUserDoesNotExistOnFindUserRented() {
	    String invalidId = "invalid-id";

	    when(currentUserProvider.currentUserId()).thenReturn(invalidId);
	    doThrow(UserNotFoundException.class)
	        .when(userService).requireExistence(invalidId);

	    assertThatThrownBy(() -> rentalService.findUserRented())
	        .isInstanceOf(UserNotFoundException.class);

	    verify(currentUserProvider).currentUserId();
	    verify(userService).requireExistence(invalidId);
	    verifyNoInteractions(queryRepository);
	}
	
	@Test
	void shouldFindUserRentals() {
		String ownerId = owner.getId();
		
		List<RentalDisplayDTO> expected = List.of(RentalTestFactory.toRentalDisplayDTO(rental));
		
		when(currentUserProvider.currentUserId()).thenReturn(ownerId);
		when(queryRepository.findUserRentals(ownerId)).thenReturn(expected);
		
		List<RentalDisplayDTO> result = rentalService.findUserRentals();
		
		assertThat(result).isEqualTo(expected);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).requireExistence(ownerId);
	}
	
	@Test
	void shouldThrowWhenUserDoesNotExistOnFindUserRentals() {
	    String invalidId = "invalid-id";

	    when(currentUserProvider.currentUserId()).thenReturn(invalidId);
	    doThrow(UserNotFoundException.class)
	        .when(userService).requireExistence(invalidId);

	    assertThatThrownBy(() -> rentalService.findUserRentals())
	        .isInstanceOf(UserNotFoundException.class);

	    verify(currentUserProvider).currentUserId();
	    verify(userService).requireExistence(invalidId);
	    verifyNoInteractions(queryRepository);
	}
	
	@Test
	void shouldAddRental() {
		String renterId = renter.getId();
		String itemId = item2.getId();
		RentalPeriod period = RentalPeriod.MONTHLY;
		
		RentalRequestDTO request = RentalTestFactory.newRentalRequest(itemId, period.name());
		ItemRentedContext context = ItemTestFactory.toItemRentedContext(item2, item2.getPickupAddress(), owner);
		
		BigDecimal basePrice = context.getItemInfo().getBasePrice();
		BigDecimal finalPrice = basePrice.multiply(period.getMultiplier());
		
		Rental mappedRental = RentalTestFactory.create(renter, renterId, context, period, RentalStatus.CREATED, finalPrice);
		Rental persistedRental = RentalTestFactory.toPersisted(mappedRental);
		RentalCreatedDTO expected = RentalTestFactory.toCreatedDTO(persistedRental);
		
		when(currentUserProvider.currentUserId()).thenReturn(renterId);
		when(userService.getValidReference(renterId)).thenReturn(renter);
		when(itemService.getItemRentedContext(itemId)).thenReturn(context);
		when(mapper.create(renter, renterId, context, period, RentalStatus.CREATED, finalPrice))
		.thenReturn(mappedRental);
		when(rentalRepository.save(mappedRental)).thenReturn(persistedRental);
		when(mapper.toCreatedDto(persistedRental)).thenReturn(expected);
		
		RentalCreatedDTO result = rentalService.addRent(request);
		
		assertThat(result).isEqualTo(expected);
		
		verify(currentUserProvider).currentUserId();
		verify(userService).getValidReference(renterId);
		verify(rentalRepository).save(mappedRental);
	}
}
