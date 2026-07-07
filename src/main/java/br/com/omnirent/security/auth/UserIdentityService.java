package br.com.omnirent.security.auth;

import org.springframework.stereotype.Service;

import br.com.omnirent.user.UserQueryRepository;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserIdentityService {
	
	private final UserIdentityRepository identityRepository;
	
	private final UserIdentityQueryRepository queryRepository;
	
	private final UserRepository userRepository;
	
	private final UserQueryRepository userQueryRepository;

	public User resolveUser(String provider, String providerUserId, String email) {
		
	}
}
