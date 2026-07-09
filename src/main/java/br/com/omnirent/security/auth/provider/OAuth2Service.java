package br.com.omnirent.security.auth.provider;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.security.auth.ProviderUserMetadata;
import br.com.omnirent.security.auth.provider.mapper.GoogleOauth2UserMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuth2Service {
	
	private final GoogleOauth2UserMapper googleMapper;

	public ProviderUserMetadata resolveUserMetadata(AuthProvider provider, OAuth2User user) {
		ProviderUserMetadata metadata = switch (provider) {
		case GOOGLE: {
			yield googleMapper.map(provider, user);
		}
//		case GITHUB: {
//			
//			yield;
//			
//		}
		default:
			throw new ApiException(AuthenticationErrorType.UNSUPPORTED_AUTH_PROVIDER);
		};
		
		validateProviderUserMetadata(metadata);
		return metadata;
	}
	
	private void validateProviderUserMetadata(ProviderUserMetadata userInfo) {
		if (StringUtils.isBlank(userInfo.sub())) {
		    throw new ApiException(
		        AuthenticationErrorType.OAUTH_SUB_REQUIRED);
		}
		if (StringUtils.isBlank(userInfo.email())) {
		    throw new ApiException(
		        AuthenticationErrorType.OAUTH_EMAIL_REQUIRED);
		}
	}
}
