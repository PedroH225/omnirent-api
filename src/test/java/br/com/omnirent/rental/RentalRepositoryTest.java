package br.com.omnirent.rental;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import br.com.omnirent.address.AddressRepository;
import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.address.domain.AddressSnapshot;
import br.com.omnirent.address.dto.AddressSnapshotDTO;
import br.com.omnirent.category.CategoryRepository;
import br.com.omnirent.category.SubCategoryRepository;
import br.com.omnirent.category.domain.Category;
import br.com.omnirent.category.domain.SubCategory;
import br.com.omnirent.common.enums.ItemCondition;
import br.com.omnirent.common.enums.RentalPeriod;
import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.config.CacheTestConfig;
import br.com.omnirent.config.TestClockConfig;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.CategoryTestFactory;
import br.com.omnirent.factory.ItemTestFactory;
import br.com.omnirent.factory.RentalTestFactory;
import br.com.omnirent.factory.SubCategoryTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.item.ItemRepository;
import br.com.omnirent.item.domain.Item;
import br.com.omnirent.item.domain.ItemData;
import br.com.omnirent.item.domain.ItemSnapshot;
import br.com.omnirent.item.dto.ItemSnapshotDTO;
import br.com.omnirent.rental.context.RentalStatusChangeContext;
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
	CacheTestConfig.class, 
	TestClockConfig.class
	})
public class RentalRepositoryTest extends IntegrationTest {

	@Autowired
	private RentalRepository rentalRepository;
	
	@Autowired
	private RentalQueryRepository queryRepository;
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private SubCategoryRepository subRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private Clock clock;
	
	private User owner;
	private User renter;
	
	private Address ownerAddress;
	
	private Category tools;
	private SubCategory drill;
	
	private Item item;
	private Item item2;
	
	private Rental rental;
	private Rental rental2;
		
	@BeforeEach
	void setUp() {	
		owner = userRepository.save(UserTestFactory.owner());
		renter = userRepository.save(UserTestFactory.user());
		
		ownerAddress = addressRepository.save(AddressTestFactory.forUser(owner));
        tools = categoryRepository.save(CategoryTestFactory.create("Tools"));
        drill = subRepository.save(SubCategoryTestFactory.create("Drill", tools));
        
        item = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, drill,
        		"200", ItemCondition.NEW));
        
        item2 = itemRepository.save(ItemTestFactory.create(owner, ownerAddress, drill,
        		"100", ItemCondition.USED));
        
        rental = rentalRepository.save(RentalTestFactory.create(item, owner, renter, ownerAddress,
        		"300", RentalStatus.CREATED, RentalPeriod.MONTHLY, Instant.now(clock), Instant.now(clock)));
        
        ZonedDateTime zonedDate = ZonedDateTime.now(clock);
        Instant lateStartDate = zonedDate.minusDays(2).toInstant();
        Instant lateEndDate = zonedDate.minusDays(1).toInstant();
        
        rental2 = rentalRepository.save(RentalTestFactory.create(item2, owner, renter, ownerAddress,
        		"300", RentalStatus.IN_USE, RentalPeriod.MONTHLY, lateStartDate, lateEndDate));
	
	}
	
	@Test
	void shouldFindRentalDetail() {
	    Optional<RentalDetailDTO> result = queryRepository.findRentalDetail(rental.getId());

	    assertThat(result).isPresent();

	    RentalDetailDTO dto = result.get();

	    ItemSnapshotDTO itemSnpDto = dto.getItemSnapshot();
	    AddressSnapshotDTO adrsSnpDto = dto.getAddressSnapshot();
	    UserResponseDTO ownerDTO = dto.getOwner();
	    UserResponseDTO renterDto = dto.getRenter();

	    AddressSnapshot adrsSnp = rental.getAddressSnapshot();
	    AddressData adrsData = adrsSnp.getAddressData();
	    ItemSnapshot itemSnp = rental.getItemSnapshot();
	    ItemData itemData = itemSnp.getItemData();

	    assertThat(dto.getId()).isEqualTo(rental.getId());
	    assertThat(dto.getStartDate()).isEqualTo(rental.getStartDate().truncatedTo(ChronoUnit.SECONDS));
	    assertThat(dto.getEndDate()).isEqualTo(rental.getEndDate().truncatedTo(ChronoUnit.SECONDS));
	    assertThat(dto.getFinalPrice()).isEqualByComparingTo(rental.getFinalPrice());
	    assertThat(dto.getRentalStatus()).isEqualTo(rental.getRentalStatus());
	    assertThat(dto.getRentalPeriod()).isEqualTo(rental.getRentalPeriod());

	    assertThat(renterDto.getId()).isEqualTo(renter.getId());
	    assertThat(renterDto.getUsername()).isEqualTo(renter.getUsername());

	    assertThat(ownerDTO.getId()).isEqualTo(owner.getId());
	    assertThat(ownerDTO.getUsername()).isEqualTo(owner.getUsername());

	    assertThat(itemSnpDto.getId()).isEqualTo(itemSnp.getId());
	    assertThat(itemSnpDto.getName()).isEqualTo(itemSnp.getName());
	    assertThat(itemSnpDto.getBrand()).isEqualTo(itemData.getBrand());
	    assertThat(itemSnpDto.getModel()).isEqualTo(itemData.getModel());
	    assertThat(itemSnpDto.getBasePrice()).isEqualByComparingTo(itemData.getBasePrice());
	    assertThat(itemSnpDto.getItemCondition()).isEqualTo(itemData.getItemCondition());
	    assertThat(itemSnpDto.getDescription()).isEqualTo(itemData.getDescription());

	    assertThat(adrsSnpDto.getId()).isEqualTo(adrsSnp.getId());
	    assertThat(adrsSnpDto.getStreet()).isEqualTo(adrsData.getStreet());
	    assertThat(adrsSnpDto.getNumber()).isEqualTo(adrsData.getNumber());
	    assertThat(adrsSnpDto.getComplement()).isEqualTo(adrsData.getComplement());
	    assertThat(adrsSnpDto.getDistrict()).isEqualTo(adrsData.getDistrict());
	    assertThat(adrsSnpDto.getCity()).isEqualTo(adrsData.getCity());
	    assertThat(adrsSnpDto.getState()).isEqualTo(adrsData.getState());
	    assertThat(adrsSnpDto.getCountry()).isEqualTo(adrsData.getCountry());
	    assertThat(adrsSnpDto.getZipCode()).isEqualTo(adrsData.getZipCode());
	}

	@Test
	void shouldFindRentalDisplayDTO() {
	    Optional<RentalDisplayDTO> result = queryRepository.findRentalDisplayDTO(rental.getId());

	    assertThat(result).isPresent();

	    RentalDisplayDTO dto = result.stream()
	            .filter(r -> r.getId().equals(rental.getId()))
	            .findFirst()
	            .orElseThrow();
	    
	    ItemSnapshot itemSnp = rental.getItemSnapshot();

	    assertThat(dto.getId()).isEqualTo(rental.getId());
	    assertThat(dto.getStartDate()).isEqualTo(rental.getStartDate().truncatedTo(ChronoUnit.SECONDS));
	    assertThat(dto.getEndDate()).isEqualTo(rental.getEndDate().truncatedTo(ChronoUnit.SECONDS));
	    assertThat(dto.getFinalPrice()).isEqualByComparingTo(rental.getFinalPrice());
	    assertThat(dto.getRentalStatus()).isEqualTo(rental.getRentalStatus());
	    assertThat(dto.getRentalPeriod()).isEqualTo(rental.getRentalPeriod());

	    assertThat(dto.getItemId()).isEqualTo(itemSnp.getId());
	    assertThat(dto.getItemName()).isEqualTo(itemSnp.getName());

	    assertThat(dto.getRenterId()).isEqualTo(renter.getId());
	    assertThat(dto.getRenterName()).isEqualTo(renter.getName());

	    assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
	    assertThat(dto.getOwnerName()).isEqualTo(owner.getName());

	}

	@Test
	void shouldFindUserRentals() {
	    List<RentalDisplayDTO> result = queryRepository.findUserRentals(owner.getId());

	    assertThat(result).isNotEmpty();
	    assertThat(result).hasSize(2);

	    RentalDisplayDTO dto = result.stream()
	            .filter(r -> r.getId().equals(rental.getId()))
	            .findFirst()
	            .orElseThrow();

	    ItemSnapshot itemSnp = rental.getItemSnapshot();

	    assertThat(dto.getId()).isEqualTo(rental.getId());
	    assertThat(dto.getStartDate()).isEqualTo(rental.getStartDate().truncatedTo(ChronoUnit.SECONDS));
	    assertThat(dto.getEndDate()).isEqualTo(rental.getEndDate().truncatedTo(ChronoUnit.SECONDS));
	    assertThat(dto.getFinalPrice()).isEqualByComparingTo(rental.getFinalPrice());
	    assertThat(dto.getRentalStatus()).isEqualTo(rental.getRentalStatus());
	    assertThat(dto.getRentalPeriod()).isEqualTo(rental.getRentalPeriod());

	    assertThat(dto.getItemId()).isEqualTo(itemSnp.getId());
	    assertThat(dto.getItemName()).isEqualTo(itemSnp.getName());

	    assertThat(dto.getRenterId()).isEqualTo(renter.getId());
	    assertThat(dto.getRenterName()).isEqualTo(renter.getName());

	    assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
	    assertThat(dto.getOwnerName()).isEqualTo(owner.getName());

	}

	@Test
	void shouldReturnEmptyListWhenUserHasNoRentals() {
	    List<RentalDisplayDTO> result = queryRepository.findUserRentals(renter.getId());

	    assertThat(result).isEmpty();
	}
	
	@Test
	void shouldFindUserRented() {
	    List<RentalDisplayDTO> result = queryRepository.findUserRented(renter.getId());

	    assertThat(result).isNotEmpty();
	    assertThat(result).hasSize(2);

	    RentalDisplayDTO dto = result.stream()
	            .filter(r -> r.getId().equals(rental.getId()))
	            .findFirst()
	            .orElseThrow();

	    ItemSnapshot itemSnp = rental.getItemSnapshot();

	    assertThat(dto.getId()).isEqualTo(rental.getId());
	    assertThat(dto.getStartDate()).isEqualTo(rental.getStartDate().truncatedTo(ChronoUnit.SECONDS));
	    assertThat(dto.getEndDate()).isEqualTo(rental.getEndDate().truncatedTo(ChronoUnit.SECONDS));
	    assertThat(dto.getFinalPrice()).isEqualByComparingTo(rental.getFinalPrice());
	    assertThat(dto.getRentalStatus()).isEqualTo(rental.getRentalStatus());
	    assertThat(dto.getRentalPeriod()).isEqualTo(rental.getRentalPeriod());

	    assertThat(dto.getItemId()).isEqualTo(itemSnp.getId());
	    assertThat(dto.getItemName()).isEqualTo(itemSnp.getName());

	    assertThat(dto.getRenterId()).isEqualTo(renter.getId());
	    assertThat(dto.getRenterName()).isEqualTo(renter.getName());

	    assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
	    assertThat(dto.getOwnerName()).isEqualTo(owner.getName());

	}

	@Test
	void shouldReturnEmptyListWhenUserHasNoRented() {
	    List<RentalDisplayDTO> result = queryRepository.findUserRented(owner.getId());

	    assertThat(result).isEmpty();
	}

	@Test
	void shouldGetStatusChangeContext() {
	    Optional<RentalStatusChangeContext> result = queryRepository.getStatusChangeContext(rental.getId());

	    assertThat(result).isPresent();

	    RentalStatusChangeContext context = result.get();

	    assertThat(context.getId()).isEqualTo(rental.getId());
	    assertThat(context.getOwnerId()).isEqualTo(owner.getId());
	    assertThat(context.getRenterId()).isEqualTo(renter.getId());
	    assertThat(context.getRentalStatus()).isEqualTo(rental.getRentalStatus());
	    assertThat(context.getRentalPeriod()).isEqualTo(rental.getRentalPeriod());
	}

	@Test
	void shouldReturnEmptyWhenStatusChangeContextDoesNotExist() {
	    Optional<RentalStatusChangeContext> result =
	    		queryRepository.getStatusChangeContext("non-existent-rental-id");

	    assertThat(result).isEmpty();
	}

	@Test
	void shouldMarkLateRentals() {
		assertThat(rental2.getRentalStatus()).isEqualTo(RentalStatus.IN_USE);
		
		rentalRepository.markLate(RentalStatus.LATE, RentalStatus.IN_USE);
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Rental> lateRental = rentalRepository.findById(rental2.getId());
	
		assertThat(lateRental).isPresent();
		assertThat(lateRental.get().getRentalStatus()).isEqualTo(RentalStatus.LATE);
	}
	
	@Test
	void shouldUpdateRentalStatus() {
		assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.CREATED);
		
		rentalRepository.updateRentalStatus(rental.getId(), RentalStatus.IN_USE);
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<Rental> updatedRental = rentalRepository.findById(rental.getId());
	
		assertThat(updatedRental).isPresent();
		assertThat(updatedRental.get().getRentalStatus()).isEqualTo(RentalStatus.IN_USE);
	}
	
	@Test
	void shouldUpdateRentalStatusAndPeriod() {
		assertThat(rental.getRentalStatus()).isEqualTo(RentalStatus.CREATED);
		Instant startDate = Instant.now(clock);
		Instant endDate = Instant.now(clock);
		
		rentalRepository.updateRentalPeriodAndStatus(rental.getId(), RentalStatus.IN_USE,
				startDate, endDate);
	
		entityManager.flush();
		entityManager.clear();
		
		Optional<Rental> optRental = rentalRepository.findById(rental.getId());
		assertThat(optRental).isPresent();
		
		Rental updatedRental = optRental.get();
		assertThat(updatedRental.getRentalStatus()).isEqualTo(RentalStatus.IN_USE);
	    assertThat(updatedRental.getStartDate()).isEqualTo(startDate.truncatedTo(ChronoUnit.SECONDS));
	    assertThat(updatedRental.getEndDate()).isEqualTo(endDate.truncatedTo(ChronoUnit.SECONDS));
	}	
}

