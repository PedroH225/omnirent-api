package br.com.omnirent.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.user.domain.User;
import jakarta.transaction.Transactional;

@Transactional
public class UserServiceIT extends SpringIntegrationTest {
	@Autowired
    private UserRepository userRepository;
	
	private User user1;
	private User user2;

	@BeforeEach
	void setUp() {
		user1 = userRepository.save(UserTestFactory.user());
	    user2 = userRepository.save(UserTestFactory.user());
	}
	
	@Test
	void test() {
		System.out.println("userserviceit");
		System.out.println("user1: " + user1.getId());
		System.out.println("user2: " + user2.getId());

	}
}
