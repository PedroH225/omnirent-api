package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.exception.common.ValidationException;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.utils.SecurityTestUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
public class UserServiceIT extends SpringIntegrationTest {
	@Autowired
	private UserService userService;
	
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	private User user1;
	private User user2;

	private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@BeforeEach
	void setUp() {
		user1 = userRepository.save(UserTestFactory.user());
	    user2 = userRepository.save(UserTestFactory.user());
	    SecurityTestUtils.setAuthenticatedUser(user1);
	}
	
	@AfterEach
	void clearAuth() {
		SecurityTestUtils.clear();
	}
	
	@Test
	void shouldUpdateUser() {
		UserRequestDTO requestDTO = UserTestFactory.requestDto();
		
		UserDetailsDTO response = userService.update(requestDTO);
		
		entityManager.flush();
		entityManager.clear();
		
		Optional<User> optUser = userRepository.findById(response.getId());
		
		User persistedUser = optUser.orElseThrow();
		assertThat(persistedUser.getId()).isEqualTo(user1.getId());
		assertThat(persistedUser.getName()).isEqualTo(response.getName());
		assertThat(persistedUser.getEmail()).isEqualTo(response.getEmail());
		assertThat(persistedUser.getUsername()).isEqualTo(response.getUsername());
		assertThat(persistedUser.getBirthDate().format(dtf)).isEqualTo(response.getBirthDate());
	}
	
	@Test
	void shouldThrowExceptionWhenUpdatingToDuplicateFields() {
		UserRequestDTO requestDTO = UserTestFactory.requestDtoBuilder(
				user1.getName(), user2.getUsername(), user2.getEmail(), user1.getBirthDate());

		assertThatThrownBy(() -> userService.update(requestDTO))
        .isInstanceOf(ValidationException.class)
        .satisfies(ex -> {
        	ValidationException exception = (ValidationException) ex;

        	assertThat(exception.getFields())
            .anyMatch(field -> field.field().equals("username"))
            .anyMatch(field -> field.field().equals("email"));
        });
	}
}
