package br.com.omnirent.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.security.TokenService;
import br.com.omnirent.security.auth.AuthenticationService;
import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.security.event.UserLoggedInEvent;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.UserQueryRepository;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.UserValidationService;
import br.com.omnirent.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {
	
	@InjectMocks
	private AuthenticationService authenticationService;

	@Mock
	private ApplicationContext context;

	@Mock
	private UserQueryRepository queryRepository;
	
	@Mock
	private UserService userService;

	@Mock
	private TokenService tokenService;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private UserMapper mapper;

	@Mock
	private UserValidationService validationService;

	@Mock
	private SpringDomainEventPublisher eventPublisher;

	@Mock
	private Clock clock;
	
	private User user;
	
	@BeforeEach
	void setup() {
		user = UserTestFactory.persistedUser();
	}
	
	@Test
	void loginSuccess() {
		LoginDTO loginDTO = new LoginDTO("test@email.com", "password");
		HttpServletRequest request = mock(HttpServletRequest.class);
		Authentication auth = mock(Authentication.class);
		AuthenticatedUser authUser = new AuthenticatedUser("123", "test@email.com", "password", Collections.emptyList(), 1, 1);
		Instant now = Instant.now(clock);

		when(context.getBean(AuthenticationManager.class)).thenReturn(authenticationManager);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
		when(auth.getPrincipal()).thenReturn(authUser);
		when(tokenService.generateToken(authUser)).thenReturn("expected-token");
		when(request.getHeader("X-Forwarded-For")).thenReturn(null);
		when(request.getRemoteAddr()).thenReturn("127.0.0.1");
		when(request.getHeader("User-Agent")).thenReturn("Test-Agent");
		when(clock.instant()).thenReturn(now);

		Map<String, String> result = authenticationService.login(loginDTO, request);

		assertThat(result)
				.isNotNull()
				.containsEntry("token", "expected-token");

		ArgumentCaptor<UserLoggedInEvent> eventCaptor = ArgumentCaptor.forClass(UserLoggedInEvent.class);
		verify(eventPublisher).publish(eventCaptor.capture());

		ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
		        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

		verify(authenticationManager).authenticate(captor.capture());

		assertThat(captor.getValue().getPrincipal()).isEqualTo("test@email.com");
		assertThat(captor.getValue().getCredentials()).isEqualTo("password");
		
		UserLoggedInEvent capturedEvent = eventCaptor.getValue();
		assertThat(capturedEvent.userId()).isEqualTo("123");
		assertThat(capturedEvent.ip()).isEqualTo("127.0.0.1");
		assertThat(capturedEvent.userAgent()).isEqualTo("Test-Agent");
		assertThat(capturedEvent.provider()).isEqualTo(AuthProvider.LOGIN_PASSWORD);
		assertThat(capturedEvent.success()).isTrue();
		assertThat(capturedEvent.occurredAt()).isEqualTo(now);

		verify(context).getBean(AuthenticationManager.class);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(tokenService).generateToken(authUser);
		verifyNoMoreInteractions(
			    authenticationManager, tokenService, eventPublisher, context);
	}
	
	@Test
	void loginThrowsBadCredentialsException() {
		LoginDTO loginDTO = new LoginDTO("test@email.com", "wrong-password");
		HttpServletRequest request = mock(HttpServletRequest.class);

		when(context.getBean(AuthenticationManager.class)).thenReturn(authenticationManager);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
				.thenThrow(new BadCredentialsException("Bad credentials"));

		ApiException exception = assertThrowsExactly(ApiException.class, () -> 
				authenticationService.login(loginDTO, request)
		);

		ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
		        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

		verify(authenticationManager).authenticate(captor.capture());

		assertThat(captor.getValue().getPrincipal()).isEqualTo("test@email.com");
		assertThat(captor.getValue().getCredentials()).isEqualTo("wrong-password");
		
		assertThat(exception.getErrorType()).isEqualTo(AuthenticationErrorType.INVALID_CREDENTIALS.getErrorType());

		verify(context).getBean(AuthenticationManager.class);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		
		verifyNoMoreInteractions(authenticationManager, context);
		verifyNoInteractions(tokenService, eventPublisher);
	}
	
	@Test
	void loginThrowsInternalAuthenticationServiceExceptionWithApiException() {
		LoginDTO loginDTO = new LoginDTO("test@email.com", "password");
		HttpServletRequest request = mock(HttpServletRequest.class);
		ApiException apiException = new ApiException(AuthenticationErrorType.INVALID_TOKEN);
		InternalAuthenticationServiceException exception = new InternalAuthenticationServiceException("Error", apiException);

		when(context.getBean(AuthenticationManager.class)).thenReturn(authenticationManager);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(exception);

		ApiException thrown = assertThrowsExactly(ApiException.class, () -> 
				authenticationService.login(loginDTO, request)
		);

		assertThat(thrown).isSameAs(apiException);

		verify(context).getBean(AuthenticationManager.class);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		
		ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
		        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

		verify(authenticationManager).authenticate(captor.capture());

		assertThat(captor.getValue().getPrincipal()).isEqualTo("test@email.com");
		assertThat(captor.getValue().getCredentials()).isEqualTo("password");
		
		assertThat(thrown).isSameAs(apiException);
		
		verifyNoMoreInteractions(authenticationManager, context);
		verifyNoInteractions(tokenService, eventPublisher);
	}

	@Test
	void loginThrowsInternalAuthenticationServiceExceptionWithOtherException() {
		LoginDTO loginDTO = new LoginDTO("test@email.com", "password");
		HttpServletRequest request = mock(HttpServletRequest.class);
		RuntimeException otherException = new RuntimeException("Generic error");
		InternalAuthenticationServiceException exception = new InternalAuthenticationServiceException("Error", otherException);

		when(context.getBean(AuthenticationManager.class)).thenReturn(authenticationManager);
		when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(exception);

		ApiException thrown = assertThrowsExactly(ApiException.class, () -> 
				authenticationService.login(loginDTO, request)
		);

		assertThat(thrown.getErrorType()).isEqualTo(AuthenticationErrorType.AUTHENTICATION_SERVICE_ERROR.getErrorType());

		ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
		        ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);

		verify(authenticationManager).authenticate(captor.capture());

		assertThat(captor.getValue().getPrincipal()).isEqualTo("test@email.com");
		assertThat(captor.getValue().getCredentials()).isEqualTo("password");
		
		verify(context).getBean(AuthenticationManager.class);
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		
		assertThat(thrown.getErrorType())
        	.isEqualTo(AuthenticationErrorType.AUTHENTICATION_SERVICE_ERROR.getErrorType());
		
		verifyNoMoreInteractions(authenticationManager, context);
		verifyNoInteractions(tokenService, eventPublisher);
	}
	
}
