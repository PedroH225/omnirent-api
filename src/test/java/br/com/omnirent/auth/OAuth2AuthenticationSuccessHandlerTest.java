package br.com.omnirent.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.common.ApiErrorResponseWriter;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.exception.domain.apptype.UserErrorType;
import br.com.omnirent.factory.ExternalIdentityTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.security.TokenService;
import br.com.omnirent.security.auth.UserIdentityService;
import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.auth.provider.OAuth2AuthenticationSuccessHandler;
import br.com.omnirent.security.auth.provider.OAuth2Service;
import br.com.omnirent.security.auth.provider.records.ProviderUserMetadata;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.security.domain.ExternalIdentity;
import br.com.omnirent.security.event.UserLoggedInEvent;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class OAuth2AuthenticationSuccessHandlerTest {
	
	@InjectMocks
	private OAuth2AuthenticationSuccessHandler successHandler;
	
	@Mock
	private OAuth2AuthorizedClientService authorizedClientService;
	
	@Mock
	private UserIdentityService userIdentityService;

	@Mock
	private ApiErrorResponseWriter apiWriter;

	@Mock
	private UserMapper userMapper;

	@Mock
	private TokenService tokenService;
	
	@Mock
	private OAuth2Service authService;

	@Mock
	private AppProperties appProperties;
	
	@Mock
	private SpringDomainEventPublisher eventPublisher;
	
	@Mock
	private Clock clock;
	
	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private OAuth2AuthenticationToken authentication;

	@Mock
	private OAuth2User oauth2User;

	@Mock
	private OAuth2AuthorizedClient authorizedClient;

	@Mock
	private OAuth2AccessToken accessToken;
	
	private User user;
	private ExternalIdentity google;
	private ExternalIdentity github;
	private ProviderUserMetadata userInfo;
	private AuthenticatedUser authenticatedUser;
	private Instant now;

	@BeforeEach
	void setup() {
		user = UserTestFactory.persistedUser();
		google = ExternalIdentityTestFactory.google(user);
		github = ExternalIdentityTestFactory.github(user);
		
		userInfo = new ProviderUserMetadata(
				AuthProvider.GOOGLE, 
				google.getProviderUserId(), 
				google.getEmail(), 
				google.isEmailVerified(), 
				user.getName(), 
				google.getAvatarUrl(), 
				"pt-BR"
		);
		
		authenticatedUser = mock(AuthenticatedUser.class);
		now = Instant.parse("2026-07-10T23:32:03Z");
	}
	
	@Test
	void shouldAuthenticateOAuth2SuccessfullyWithGoogle() throws IOException, ServletException {
		when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");
		when(authentication.getPrincipal()).thenReturn(oauth2User);
		when(authentication.getName()).thenReturn("test-user");
		when(authorizedClientService.loadAuthorizedClient("google", "test-user")).thenReturn(authorizedClient);
		when(authorizedClient.getAccessToken()).thenReturn(accessToken);
		when(accessToken.getTokenValue()).thenReturn("mock-access-token");
		when(authService.resolveUserMetadata(AuthProvider.GOOGLE, oauth2User, "mock-access-token")).thenReturn(userInfo);
		when(userIdentityService.resolveUser(userInfo)).thenReturn(user);
		when(userMapper.toAuthUser(user)).thenReturn(authenticatedUser);
		when(tokenService.generateToken(authenticatedUser)).thenReturn("mock-jwt-token");
		when(request.getHeader("X-Forwarded-For")).thenReturn("192.0.0.1");
		when(request.getHeader("User-Agent")).thenReturn("Mozilla/5.0");
		when(clock.instant()).thenReturn(now);
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		successHandler.onAuthenticationSuccess(request, response, authentication);

		ArgumentCaptor<UserLoggedInEvent> eventCaptor = ArgumentCaptor.forClass(UserLoggedInEvent.class);
		verify(eventPublisher).publish(eventCaptor.capture());

		UserLoggedInEvent event = eventCaptor.getValue();
		assertThat(event.userId()).isEqualTo(user.getId());
		assertThat(event.provider()).isEqualTo(AuthProvider.GOOGLE);
		assertThat(event.ip()).isEqualTo("192.0.0.1");
		assertThat(event.userAgent()).isEqualTo("Mozilla/5.0");
		assertThat(event.success()).isTrue();
		assertThat(event.occurredAt()).isEqualTo(now);
		
		verify(authService).resolveUserMetadata(
			    AuthProvider.GOOGLE, oauth2User, "mock-access-token");
		
		verify(authorizedClientService)
	    	.loadAuthorizedClient("google", "test-user");
		
		verify(tokenService).generateToken(authenticatedUser);
		verify(userIdentityService).resolveUser(userInfo);
		
		verify(response).sendRedirect("http://localhost:3000/oauth/callback?token=mock-jwt-token");
	}
	
	@Test
	void shouldHandleNullRegistrationId() throws IOException, ServletException {
		when(authentication.getAuthorizedClientRegistrationId()).thenReturn(null);
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		successHandler.onAuthenticationSuccess(request, response, authentication);

		ArgumentCaptor<ApiException> exceptionCaptor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), exceptionCaptor.capture());
		
		ApiException exception = exceptionCaptor.getValue();

		assertThat(exception.getErrorType())
		        .isEqualTo(AuthenticationErrorType.OAUTH_PROVIDER_REQUIRED.getErrorType());
		
		verify(authentication).getAuthorizedClientRegistrationId();
		verify(response).sendRedirect("http://localhost:3000/oauth/callback?error=OAUTH_PROVIDER_REQUIRED");
		verifyNoInteractions(userMapper, authorizedClientService, authService, tokenService, userIdentityService, eventPublisher);
	}
	
	@Test
	void shouldHandleUnsupportedProvider() throws IOException, ServletException {
		when(authentication.getAuthorizedClientRegistrationId()).thenReturn("invalid_provider");
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		successHandler.onAuthenticationSuccess(request, response, authentication);

		ArgumentCaptor<ApiException> exceptionCaptor = ArgumentCaptor.forClass(ApiException.class);
		verify(apiWriter).onApiError(eq(request), eq(response), exceptionCaptor.capture());
		
		ApiException exception = exceptionCaptor.getValue();

		assertThat(exception.getErrorType())
		        .isEqualTo(AuthenticationErrorType.UNSUPPORTED_AUTH_PROVIDER.getErrorType());
		
		verify(authentication).getAuthorizedClientRegistrationId();
		
		verify(response).sendRedirect("http://localhost:3000/oauth/callback?error=UNSUPPORTED_AUTH_PROVIDER");
		verifyNoInteractions(userMapper, authorizedClientService, authService, tokenService, userIdentityService, eventPublisher);
	}
	
	@Test
	void shouldHandleApiExceptionWhenResolveUserMetadataFails() throws IOException, ServletException {
		when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");
		when(authentication.getPrincipal()).thenReturn(oauth2User);
		when(authentication.getName()).thenReturn("test-user");
		when(authorizedClientService.loadAuthorizedClient("google", "test-user")).thenReturn(authorizedClient);
		when(authorizedClient.getAccessToken()).thenReturn(accessToken);
		when(accessToken.getTokenValue()).thenReturn("mock-access-token");

		ApiException apiException = new ApiException(AuthenticationErrorType.OAUTH_EMAIL_REQUIRED);
		when(authService.resolveUserMetadata(AuthProvider.GOOGLE, oauth2User, "mock-access-token")).thenThrow(apiException);
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		successHandler.onAuthenticationSuccess(request, response, authentication);
		
		verify(authService).resolveUserMetadata(
			    AuthProvider.GOOGLE, oauth2User, "mock-access-token");
		
		verify(apiWriter).onApiError(request, response, apiException);
		verify(response).sendRedirect("http://localhost:3000/oauth/callback?error=" + apiException.getErrorCode());
		verifyNoInteractions(tokenService, eventPublisher, userIdentityService, userMapper);
	}

	@Test
	void shouldHandleApiExceptionWhenResolveUserFails() throws IOException, ServletException {
		when(authentication.getAuthorizedClientRegistrationId()).thenReturn("google");
		when(authentication.getPrincipal()).thenReturn(oauth2User);
		when(authentication.getName()).thenReturn("test-user");
		when(authorizedClientService.loadAuthorizedClient("google", "test-user")).thenReturn(authorizedClient);
		when(authorizedClient.getAccessToken()).thenReturn(accessToken);
		when(accessToken.getTokenValue()).thenReturn("mock-access-token");
		when(authService.resolveUserMetadata(AuthProvider.GOOGLE, oauth2User, "mock-access-token")).thenReturn(userInfo);

		ApiException apiException = new ApiException(UserErrorType.INACTIVE);
		when(userIdentityService.resolveUser(userInfo)).thenThrow(apiException);
		when(appProperties.frontUrl()).thenReturn("http://localhost:3000");

		successHandler.onAuthenticationSuccess(request, response, authentication);
		
		verify(authService).resolveUserMetadata(
			    AuthProvider.GOOGLE, oauth2User, "mock-access-token");
		
		verify(apiWriter).onApiError(request, response, apiException);
		verify(response).sendRedirect("http://localhost:3000/oauth/callback?error=" + apiException.getErrorCode());
		verifyNoInteractions(tokenService, eventPublisher, userMapper);
	}
}



