package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.transaction.Transactional;

@Transactional
public class UserServiceIT extends SpringIntegrationTest {
	@Autowired
	private UserService userService;
	
	@Autowired
    private UserRepository userRepository;
	
	private User user1;
	private User user2;

	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@BeforeEach
	void setUp() {
		user1 = userRepository.save(UserTestFactory.user());
	    user2 = userRepository.save(UserTestFactory.user());
	    SecurityTestUtils.setAuthenticatedUser(user1.getId());
	}
	
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}
	
	@Test
	void shouldUpdateUser() {
		UserRequestDTO requestDTO = UserTestFactory.requestDto();
		
		UserDetailsDTO response = userService.update(requestDTO);
		
		Optional<User> optUser = userRepository.findById(response.getId());
		
		assertThat(optUser).isPresent();
		
		User persistedUser = optUser.get();
		assertThat(persistedUser.getId()).isEqualTo(user1.getId());
		assertThat(persistedUser.getName()).isEqualTo(requestDTO.name());
		assertThat(persistedUser.getEmail()).isEqualTo(requestDTO.email());
		assertThat(persistedUser.getUsername()).isEqualTo(requestDTO.username());
		assertThat(persistedUser.getBirthDate()).isEqualTo(requestDTO.birthDate());
	}
}
