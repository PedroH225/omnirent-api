package br.com.omnirent.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private UserMapper mapper;
	
	private User user1;
	private User user2;

	@BeforeEach
	void setUp() {
		user1 = UserTestFactory.persistedUser();
	    user2 = UserTestFactory.persistedUser();
	}
	
	@Test
	void test() {
		System.out.println("userservicetest");
		System.out.println("user1: " + user1.getId());
		System.out.println("user2: " + user2.getId());

	}
}
