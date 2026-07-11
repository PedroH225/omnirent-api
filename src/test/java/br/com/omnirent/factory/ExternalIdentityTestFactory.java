package br.com.omnirent.factory;

import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.auth.provider.records.ProviderUserMetadata;
import br.com.omnirent.security.domain.ExternalIdentity;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.utils.Sequence;

public final class ExternalIdentityTestFactory {

	private ExternalIdentityTestFactory() {}

	public static ExternalIdentity google(User user) {
		return new ExternalIdentity(AuthProvider.GOOGLE, 
				Sequence.nextString("google-sub"), 
				user.getEmail(), true, 
				"https://avatar.example.com/image.png", 
				user);
	}
	
	public static ExternalIdentity google(User user,ProviderUserMetadata userMetadata) {
		return new ExternalIdentity(AuthProvider.GOOGLE, 
				userMetadata.sub(), userMetadata.email(), userMetadata.emailVerified(),
				userMetadata.picture(), user);
	}
	
	public static ExternalIdentity github(User user) {
	    return new ExternalIdentity(
	        AuthProvider.GITHUB,
	        Sequence.nextString("github-id"),
	        user.getEmail(),true,
	        "https://avatars.githubusercontent.com/u/1",
	        user
	    );
	}
	
	public static ProviderUserMetadata createMetadata(User user, AuthProvider authProvider) {
		String sub = String.format("%s_%s",
				authProvider.name().toLowerCase(), user.getId());
		
		return new ProviderUserMetadata(authProvider, sub, user.getEmail(),
				true, user.getName(), "user_picture_" + user.getId(), "pt-BR");
	}
}
