package br.com.omnirent.security.auth.provider;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.common.ApiErrorResponseWriter;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.security.TokenService;
import br.com.omnirent.security.auth.UserIdentityService;
import br.com.omnirent.security.auth.provider.records.ProviderUserMetadata;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.security.event.UserLoggedInEvent;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final OAuth2AuthorizedClientService authorizedClientService;
	
	private final UserIdentityService userIdentityService;

	private final ApiErrorResponseWriter apiWriter;

	private final UserMapper userMapper;

	private final TokenService tokenService;
	
	private final OAuth2Service authService;

	private final AppProperties appProperties;
	
	private final SpringDomainEventPublisher eventPublisher;
	
	private final Clock clock;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		try {
			OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;

			AuthProvider provider = resolveProvider(oauth);
			OAuth2User principal = oauth.getPrincipal();

			OAuth2AuthorizedClient authorizedClient =
			        authorizedClientService.loadAuthorizedClient(
			                oauth.getAuthorizedClientRegistrationId(),
			                authentication.getName()
			        );
			
			String accessToken = authorizedClient
			        .getAccessToken()
			        .getTokenValue();
			
			ProviderUserMetadata userInfo = authService
					.resolveUserMetadata(provider, principal, accessToken);
			
			User user = userIdentityService.resolveUser(userInfo);

			UserDetails authenticatedUser = userMapper.toAuthUser(user);

			String token = tokenService.generateToken((AuthenticatedUser) authenticatedUser);

			String ip = extractIp(request);
			String userAgent = request.getHeader("User-Agent");
			
			eventPublisher.publish(new UserLoggedInEvent(
					user.getId(), ip, userAgent, provider, true, Instant.now(clock)));
			
			response.sendRedirect(String.format("%s/oauth/callback?token=%s",
					appProperties.frontUrl(), token));

		} catch (IOException e) {
			log.error("Failed to redirect after OAuth2 authentication", e);
		} catch (ApiException ex) {
			apiWriter.onApiError(request, response, ex);
			
			response.sendRedirect(String.format("%s/oauth/callback?error=%s", 
					appProperties.frontUrl(), ex.getErrorCode()));
		}
	}
	
	private AuthProvider resolveProvider(OAuth2AuthenticationToken oauth) {
		String registrationId = oauth.getAuthorizedClientRegistrationId();
		if (registrationId == null) {
			throw new ApiException(AuthenticationErrorType.OAUTH_PROVIDER_REQUIRED);
		}
		
		try {
		    return AuthProvider.valueOf(registrationId.toUpperCase());
		} catch (IllegalArgumentException e) {
		    throw new ApiException(AuthenticationErrorType.UNSUPPORTED_AUTH_PROVIDER);
		}
	}
	
	private String extractIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");

		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}

		return request.getRemoteAddr();
	}
}
