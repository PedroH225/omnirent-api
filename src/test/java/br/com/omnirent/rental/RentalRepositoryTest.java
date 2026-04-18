package br.com.omnirent.rental;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.format.DateTimeFormatter;
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
import br.com.omnirent.rental.domain.Rental;
import br.com.omnirent.rental.dto.RentalDetailDTO;
import br.com.omnirent.rental.dto.RentalDisplayDTO;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserResponseDTO;
import jakarta.transaction.Transactional;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CacheTestConfig.class)
public class RentalRepositoryTest extends IntegrationTest {

	@Autowired
	private RentalRepository rentalRepository;
	
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
	
	private User owner;
	private User renter;
	
	private Address ownerAddress;
	
	private Category tools;
	private SubCategory drill;
	
	private Item item;
	private Item item2;
	
	private Rental rental;
	
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	
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
        		"300", RentalStatus.ACTIVE, RentalPeriod.MONTHLY));
	}
	
	@Test
	void shouldFindRentalDetail() {
	    Optional<RentalDetailDTO> result = rentalRepository.findRentalDetail(rental.getId());

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
	    assertThat(dto.getStartDate()).isEqualTo(dtf.format(rental.getStartDate()));
	    assertThat(dto.getEndDate()).isEqualTo(dtf.format(rental.getEndDate()));
	    assertThat(dto.getFinalPrice()).isEqualByComparingTo(rental.getFinalPrice());
	    assertThat(dto.getRentalStatus()).isEqualTo(rental.getRentalStatus().toString());
	    assertThat(dto.getRentalPeriod()).isEqualTo(rental.getRentalPeriod().toString());

	    assertThat(renterDto.getId()).isEqualTo(renter.getId());
	    assertThat(renterDto.getUsername()).isEqualTo(renter.getDisplayUsername());

	    assertThat(ownerDTO.getId()).isEqualTo(owner.getId());
	    assertThat(ownerDTO.getUsername()).isEqualTo(owner.getDisplayUsername());

	    assertThat(itemSnpDto.getId()).isEqualTo(itemSnp.getId());
	    assertThat(itemSnpDto.getName()).isEqualTo(itemSnp.getName());
	    assertThat(itemSnpDto.getBrand()).isEqualTo(itemData.getBrand());
	    assertThat(itemSnpDto.getModel()).isEqualTo(itemData.getModel());
	    assertThat(itemSnpDto.getBasePrice()).isEqualByComparingTo(itemData.getBasePrice());
	    assertThat(itemSnpDto.getItemCondition()).isEqualTo(itemData.getItemCondition().toString());
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
	    Optional<RentalDisplayDTO> result = rentalRepository.findRentalDisplayDTO(rental.getId());

	    assertThat(result).isPresent();

	    RentalDisplayDTO dto = result.get();
	    
	    ItemSnapshot itemSnp = rental.getItemSnapshot();

	    assertThat(dto.getId()).isEqualTo(rental.getId());
	    assertThat(dto.getStartDate()).isEqualTo(dtf.format(rental.getStartDate()));
	    assertThat(dto.getEndDate()).isEqualTo(dtf.format(rental.getEndDate()));
	    assertThat(dto.getFinalPrice()).isEqualByComparingTo(rental.getFinalPrice());
	    assertThat(dto.getRentalStatus()).isEqualTo(rental.getRentalStatus().toString());
	    assertThat(dto.getRentalPeriod()).isEqualTo(rental.getRentalPeriod().toString());

	    assertThat(dto.getItemId()).isEqualTo(itemSnp.getId());
	    assertThat(dto.getItemName()).isEqualTo(itemSnp.getName());

	    assertThat(dto.getRenterId()).isEqualTo(renter.getId());
	    assertThat(dto.getRenterName()).isEqualTo(renter.getName());

	    assertThat(dto.getOwnerId()).isEqualTo(owner.getId());
	    assertThat(dto.getOwnerName()).isEqualTo(owner.getName());

	    assertThat(dto.getCreatedAt()).isEqualTo(dtf.format(rental.getCreatedAt()));
	}
}

