package br.com.omnirent.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.omnirent.config.global.GlobalConfigHolder;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.security.CookieService;
import br.com.omnirent.security.auth.AuthenticationService;
import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
	
	@Autowired
	private GlobalConfigHolder globalConfigHolder;
	
	@Autowired
	private CookieService cookieService;
	
	private User user;
	
	@BeforeEach
	void setup() {
		user = userRepository.save(UserTestFactory.user());
	}
	
	@Test
	void shouldLogin_WithValidCredentials() {
		String rawPassword = "validPassword123";
		user.setPassword(passwordEncoder.encode(rawPassword));
		userRepository.save(user);
		
		entityManager.flush();
		entityManager.clear();

		LoginDTO loginDTO = new LoginDTO(user.getEmail(), rawPassword);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);
		

		when(request.getHeader("X-Forwarded-For")).thenReturn(null);
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getHeader("User-Agent")).thenReturn("Integration-Test-Agent");
		
		authenticationService.login(loginDTO, request, httpResponse);
	}
	
	@Test
	void login_WithInvalidCredentials_ShouldThrowApiException() {
		String rawPassword = "validPassword123";
		user.setPassword(passwordEncoder.encode(rawPassword));
		userRepository.save(user);
		
		entityManager.flush();
		entityManager.clear();

		LoginDTO loginDTO = new LoginDTO(user.getEmail(), "wrongPassword");
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);

		ApiException exception = assertThrowsExactly(ApiException.class, () -> 
			authenticationService.login(loginDTO, request, httpResponse)
		);

		assertThat(exception.getErrorType()).isEqualTo(AuthenticationErrorType.INVALID_CREDENTIALS.getErrorType());
	}
	
	@Test
	void login_NonexistentCredentials_ShouldThrowApiException() {
	    LoginDTO loginDTO = new LoginDTO("nonexistent@email.com", "anyPassword");
	    HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse httpResponse = mock(HttpServletResponse.class);

	    ApiException exception = assertThrowsExactly(ApiException.class, () ->
	            authenticationService.login(loginDTO, request, httpResponse)
	    );

	    assertThat(exception.getErrorType())
	            .isEqualTo(AuthenticationErrorType.INVALID_CREDENTIALS.getErrorType());
	}
}
