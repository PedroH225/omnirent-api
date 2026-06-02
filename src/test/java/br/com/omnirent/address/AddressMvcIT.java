package br.com.omnirent.address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.domain.AddressData;
import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringMvcIntegration;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
public class AddressMvcIT extends SpringMvcIntegration {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule());
	
	private static final String ADDRESS_PREFIX = "/address";
	
	private User user1;
	
	@BeforeEach
	void setUp() {
		user1 = userRepository.save(UserTestFactory.owner());
	    SecurityTestUtils.setAuthenticatedUser(user1.getId());
	}
	
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}
	
	@Test
	void shouldSanitizeNewAddressFields() throws Exception {
		AddressRequestDTO dirty = new AddressRequestDTO(
		        null,
		        "  Rua    das    Flores  ",
		        "  1	23  ",
		        "  Apartamento    45  ",
		        "  Centro    Histórico  ",
		        "  SÃO    PAULO  ",
		        "  SÃO    PAULO  ",
		        "  BRASIL  ",
		        "  012	34-567  "
		);
		
		String payload = objectMapper.writeValueAsString(dirty);

		String response = mockMvc.perform(post(ADDRESS_PREFIX)
				.with(SecurityTestUtils.auth(user1.getId()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(payload))
		.andExpect(status().isOk())
		.andReturn()
		.getResponse()
		.getContentAsString();
		
		AddressResponseDTO addressDTO = objectMapper.readValue(response, AddressResponseDTO.class);
	
		Address persistedAddress = addressRepository.findById(addressDTO.getId()).orElseThrow();
		
		AddressData addressData = persistedAddress.getAddressData();

		assertThat(addressData.getStreet())
		        .isEqualTo("Rua das Flores");

		assertThat(addressData.getNumber())
		        .isEqualTo("123");

		assertThat(addressData.getComplement())
		        .isEqualTo("Apartamento 45");

		assertThat(addressData.getDistrict())
		        .isEqualTo("Centro Histórico");

		assertThat(addressData.getCity())
		        .isEqualTo("SÃO PAULO");

		assertThat(addressData.getState())
		        .isEqualTo("SÃO PAULO");

		assertThat(addressData.getCountry())
		        .isEqualTo("BRASIL");

		assertThat(addressData.getZipCode())
		        .isEqualTo("01234-567");
		
	}
}
