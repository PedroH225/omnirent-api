package br.com.omnirent.security.auth.provider;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.common.ApiErrorResponseWriter;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.UserErrorType;
import br.com.omnirent.security.TokenService;
import br.com.omnirent.security.auth.ProviderUserMetadata;
import br.com.omnirent.security.auth.UserIdentityService;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private final UserIdentityService userIdentityService;

	private final ApiErrorResponseWriter apiWriter;

	private final UserMapper userMapper;

	private final TokenService tokenService;

	private final AppProperties appProperties;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		try {
			OAuth2AuthenticationToken oauth = (OAuth2AuthenticationToken) authentication;

			OAuth2User principal = oauth.getPrincipal();

			ProviderUserMetadata userInfo = new ProviderUserMetadata(
					AuthProvider.valueOf(oauth.getAuthorizedClientRegistrationId().toUpperCase()),
					principal.getAttribute("sub"), 
					principal.getAttribute("email"),
					Boolean.TRUE.equals(principal.getAttribute("email_verified")), 
					principal.getAttribute("name"),
					principal.getAttribute("picture"), 
					principal.getAttribute("locale"));

			User user = userIdentityService.resolveUser(userInfo);

			UserDetails authenticatedUser = userMapper.toAuthUser(user);

			String token = tokenService.generateToken((AuthenticatedUser) authenticatedUser);

			response.sendRedirect(String.format("%s/oauth/callback?token=%s", appProperties.frontUrl(), token));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ApiException ex) {
			apiWriter.onApiError(request, response, ex);
			
			response.sendRedirect(String.format("%s/oauth/callback?error=%s", 
					appProperties.frontUrl(), ex.getErrorCode()));
		}
	}
}
