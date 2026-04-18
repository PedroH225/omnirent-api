package br.com.omnirent.address;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.address.domain.Address;
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
	void test() {
		
	}
}
