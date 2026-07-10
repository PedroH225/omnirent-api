package br.com.omnirent.security.auth.provider.mapper;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.auth.provider.records.ProviderUserMetadata;

@Component
public class GoogleOauth2UserMapper {

	public ProviderUserMetadata map(AuthProvider provider, OAuth2User principal) {
		return new ProviderUserMetadata(
				provider,
				principal.getAttribute("sub"), 
				principal.getAttribute("email"),
				Boolean.TRUE.equals(principal.getAttribute("email_verified")), 
				principal.getAttribute("name"),
				principal.getAttribute("picture"), 
				principal.getAttribute("locale"));
	}
}
