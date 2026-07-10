package br.com.omnirent.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.security.auth.AuthenticationService;
import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Transactional
public class AuthenticationServiceIT extends SpringIntegrationTest {

	@Autowired
	private UserRepository userRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private User user;
	
	@BeforeEach
	void setup() {
		user = userRepository.save(UserTestFactory.user());
	}
	
	@Test
	void login_WithValidCredentials_ShouldReturnToken() {
		String rawPassword = "validPassword123";
		user.setPassword(passwordEncoder.encode(rawPassword));
		userRepository.save(user);
		
		entityManager.flush();
		entityManager.clear();

		LoginDTO loginDTO = new LoginDTO(user.getEmail(), rawPassword);
		HttpServletRequest request = mock(HttpServletRequest.class);
		
		when(request.getHeader("X-Forwarded-For")).thenReturn(null);
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getHeader("User-Agent")).thenReturn("Integration-Test-Agent");

		Map<String, String> response = authenticationService.login(loginDTO, request);

		assertThat(response).isNotNull();
		assertThat(response).containsOnlyKeys("token");
		assertThat(response.get("token")).isNotBlank();
	}
}
