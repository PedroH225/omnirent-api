package br.com.omnirent.address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.github.dockerjava.core.dockerfile.DockerfileStatement.Add;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.exception.domain.AddressNotFoundException;
import br.com.omnirent.exception.domain.UserNotFoundException;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import jakarta.transaction.Transactional;

@Transactional
public class AddressServiceIT extends SpringIntegrationTest {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private AddressService addressService;
	
	private User user;
	
	private Address userAddress;
	
	@BeforeEach
	void setUp() {
		user = userRepository.save(UserTestFactory.user());
		userAddress = addressRepository.save(AddressTestFactory.forUser(user));
	}
 	
	@Test
	void shouldAddAddress() {
		AddressRequestDTO addressRequestDTO = AddressTestFactory.toRequestDTO(userAddress);
		
		AddressResponseDTO response = addressService.addAddress(addressRequestDTO, user.getId());
		
		assertThat(response).isNotNull();
		assertThat(response.getId()).isNotNull();
		
		Optional<Address> optPersisted = addressRepository.findById(response.getId());

		assertThat(optPersisted).isPresent();
		
		Address persisted = optPersisted.get();
	    assertThat(persisted.getUserId()).isEqualTo(user.getId());
	    assertThat(persisted.getCreatedAt()).isNotNull();
	    assertThat(persisted.getUpdatedAt()).isNotNull();

	    AddressData data = persisted.getAddressData();

	    assertThat(data.getStreet()).isEqualTo(addressRequestDTO.street());
	    assertThat(data.getNumber()).isEqualTo(addressRequestDTO.number());
	    assertThat(data.getComplement()).isEqualTo(addressRequestDTO.complement());
	    assertThat(data.getDistrict()).isEqualTo(addressRequestDTO.district());
	    assertThat(data.getCity()).isEqualTo(addressRequestDTO.city());
	    assertThat(data.getState()).isEqualTo(addressRequestDTO.state());
	    assertThat(data.getCountry()).isEqualTo(addressRequestDTO.country());
	    assertThat(data.getZipCode()).isEqualTo(addressRequestDTO.zipCode());
	}
	
	@Test
	void shouldThrowWhenInvalidUser() {
		AddressRequestDTO addressRequestDTO = AddressTestFactory.toRequestDTO(userAddress);
		
		assertThatThrownBy(() -> addressService.addAddress(addressRequestDTO, "123"))
		.isInstanceOf(UserNotFoundException.class);
	}
	
	@Test
	void shouldUpdateAddress() {
	    Address addressBefore = AddressTestFactory.makeCopy(userAddress);
	    AddressRequestDTO requestDTO = AddressTestFactory.updatedRequestDTO(userAddress);

	    AddressResponseDTO response = addressService.updateAddress(requestDTO);

	    Optional<Address> optPersisted = addressRepository.findById(response.getId());
	    assertThat(optPersisted).isPresent();

	    Address persisted = optPersisted.get();
	    assertThat(persisted.getUserId()).isEqualTo(user.getId());
	    assertThat(persisted.getCreatedAt()).isEqualTo(addressBefore.getCreatedAt());
	    assertThat(persisted.getUpdatedAt()).isNotNull();

	    AddressData data = persisted.getAddressData();
	    AddressData beforeData = addressBefore.getAddressData();

	    assertThat(data.getStreet()).isEqualTo(requestDTO.street());
	    assertThat(data.getNumber()).isEqualTo(requestDTO.number());
	    assertThat(data.getComplement()).isEqualTo(requestDTO.complement());
	    assertThat(data.getDistrict()).isEqualTo(requestDTO.district());
	    assertThat(data.getCity()).isEqualTo(requestDTO.city());
	    assertThat(data.getState()).isEqualTo(requestDTO.state());
	    assertThat(data.getCountry()).isEqualTo(requestDTO.country());
	    assertThat(data.getZipCode()).isEqualTo(requestDTO.zipCode());

	    assertThat(data.getStreet()).isNotEqualTo(beforeData.getStreet());
	    assertThat(data.getNumber()).isNotEqualTo(beforeData.getNumber());
	    assertThat(data.getComplement()).isNotEqualTo(beforeData.getComplement());
	    assertThat(data.getDistrict()).isNotEqualTo(beforeData.getDistrict());
	    assertThat(data.getCity()).isNotEqualTo(beforeData.getCity());
	    assertThat(data.getState()).isNotEqualTo(beforeData.getState());
	    assertThat(data.getCountry()).isNotEqualTo(beforeData.getCountry());
	    assertThat(data.getZipCode()).isNotEqualTo(beforeData.getZipCode());
	}
	
	@Test
	void shouldThrowWhenInvalidAddress() {
		AddressRequestDTO addressRequestDTO = AddressTestFactory.toInvalidRequestDTO(userAddress);

		assertThatThrownBy(() -> addressService.updateAddress(addressRequestDTO))
		.isInstanceOf(AddressNotFoundException.class);
	}
	
	@Test
	void shouldDeleteAddress() {
		addressService.deleteAddress(userAddress.getId());
		
		Optional<Address> optFindAddress = addressRepository.findById(userAddress.getId());
		
		assertThat(optFindAddress).isNotPresent();
	}
}
