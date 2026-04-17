package br.com.omnirent.address;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;

@SpringBootTest
public class AddressRepositoryTest extends IntegrationTest {

	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
    private UserRepository userRepository;
	
	@Test
    void shouldFindAddressByUserId() {
		User user = new User("addressUser", "addressUser", "addressUser@email.com", "addressUser", LocalDate.now(), 1, 1);
        user = userRepository.save(user);

        AddressData addressData = new AddressData();
        addressData.setStreet("Rua Azul");
        addressData.setNumber("450");
        addressData.setComplement("Apto 12");
        addressData.setDistrict("Centro");
        addressData.setCity("Campinas");
        addressData.setState("SP");
        addressData.setCountry("Brazil");
        addressData.setZipCode("13000-000");

        Address address = new Address();
        address.setUserId(user.getId());
        address.setAddressData(addressData);

        addressRepository.save(address);

        List<AddressResponseDTO> find =
                addressRepository.findAddressByUser(user.getId());

        assertThat(find).isNotEmpty();

        AddressResponseDTO addressDTO = find.get(0);

        assertThat(addressDTO.getId()).isNotNull();
        assertThat(addressDTO.getStreet()).isEqualTo("Rua Azul");
        assertThat(addressDTO.getNumber()).isEqualTo("450");
        assertThat(addressDTO.getCity()).isEqualTo("Campinas");
        assertThat(addressDTO.getZipCode()).isEqualTo("13000-000");
    }
}
