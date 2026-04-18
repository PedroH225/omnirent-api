package br.com.omnirent.address;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.address.domain.Address;
import br.com.omnirent.address.dto.AddressRequestDTO;
import br.com.omnirent.address.dto.AddressResponseDTO;
import br.com.omnirent.factory.AddressTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class AddressServiceTest {

	@InjectMocks
	private AddressService addressService;
	
	@Mock
	private AddressRepository addressRepository;
	
	@Mock
    private UserService userService;
	
	@Mock
    private AddressMapper mapper;
	
	private User user;

	private User user2;
	
	private Address userAddress;
	
	private Address userAddress2;
	
	@BeforeEach
	void setUp() {
		user = UserTestFactory.persistedUser();
		userAddress = AddressTestFactory.forPersistedUser(user);
		userAddress2 = AddressTestFactory.forPersistedUser(user);

		user2 = UserTestFactory.persistedUser();
	}
	
	@Test
	void shouldReturnUserAddresses() {
	    String userId = user.getId();

	    AddressResponseDTO dto1 = AddressTestFactory.toAddressDto(userAddress);
	    
	    AddressResponseDTO dto2 = AddressTestFactory.toAddressDto(userAddress2);

	    List<AddressResponseDTO> expected = List.of(dto1, dto2);

	    when(addressRepository.findAddressByUser(userId))
	            .thenReturn(expected);

	    List<AddressResponseDTO> result =
	            addressService.getUserAddresses(userId);

	    assertThat(result).isEqualTo(expected);

	    verify(addressRepository).findAddressByUser(userId);
	    verifyNoMoreInteractions(addressRepository);
	}
	
	@Test
	void shouldAddAddress() {
	    AddressRequestDTO addressDTO = AddressTestFactory.toRequestDTO(userAddress);
	    String userId = user.getId();

	    AddressResponseDTO responseDTO = AddressTestFactory.toAddressDto(userAddress);

	    doNothing().when(userService).requireExistence(userId);
	    when(mapper.fromAddressDTO(addressDTO, userId)).thenReturn(userAddress);
	    when(addressRepository.save(userAddress)).thenReturn(userAddress);
	    when(mapper.toDto(userAddress)).thenReturn(responseDTO);

	    AddressResponseDTO result = addressService.addAddress(addressDTO, userId);

	    assertThat(result).isEqualTo(responseDTO);

	    verify(userService).requireExistence(userId);
	    verify(mapper).fromAddressDTO(addressDTO, userId);
	    verify(addressRepository).save(userAddress);
	    verify(mapper).toDto(userAddress);
	    verifyNoMoreInteractions(userService, mapper, addressRepository);
	}
}
