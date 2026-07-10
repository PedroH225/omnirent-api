package br.com.omnirent.security.auth.provider.mapper;

import java.util.Objects;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.auth.provider.records.GithubEmailMetadata;
import br.com.omnirent.security.auth.provider.records.ProviderUserMetadata;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GithubOauth2UserMapper {
	
	private final GithubEmailClient githubEmailClient;

	public ProviderUserMetadata map(AuthProvider provider, OAuth2User principal, String accessToken) {
		GithubEmailMetadata emailMetadata = githubEmailClient.findPrimaryEmail(accessToken);
		
		return new ProviderUserMetadata(
				provider,
				Objects.toString(principal.getAttribute("id"), null), 
				emailMetadata.email(),
				Boolean.TRUE.equals(emailMetadata.verified()), 
				principal.getAttribute("name"),
				principal.getAttribute("avatar_url"), 
				null);
	}
}
