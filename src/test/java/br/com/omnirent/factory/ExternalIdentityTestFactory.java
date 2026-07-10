package br.com.omnirent.factory;

import br.com.omnirent.security.auth.provider.AuthProvider;
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
	
	public static ExternalIdentity github(User user) {
	    return new ExternalIdentity(
	        AuthProvider.GITHUB,
	        Sequence.nextString("github-id"),
	        user.getEmail(),true,
	        "https://avatars.githubusercontent.com/u/1",
	        user
	    );
	}
}
