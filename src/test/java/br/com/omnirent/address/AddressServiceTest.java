package br.com.omnirent.address;

import static org.assertj.core.api.Assertions.assertThat;
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
}
