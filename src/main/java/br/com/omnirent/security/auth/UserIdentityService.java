package br.com.omnirent.security.auth;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.UserErrorType;
import br.com.omnirent.security.domain.ExternalIdentity;
import br.com.omnirent.user.UserQueryRepository;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.domain.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserIdentityService {
	
	private final UserIdentityRepository identityRepository;
	
	private final UserIdentityQueryRepository identityQueryRepository;
	
	private final UserService userService;
	
	private final UserQueryRepository userQueryRepository;

	public User resolveUser(ProviderUserMetadata userInfo) {
	    Optional<ExternalIdentity> identity =identityQueryRepository
	    		.findByProviderAndProviderUserId(userInfo.provider(), userInfo.sub());

	    if (identity.isPresent()) {
	    	User found = identity.get().getUser();
			requireActive(found);
	        return found;
	    }
		
		Optional<User> optUser = userQueryRepository.findByEmail(userInfo.email());
		if (optUser.isEmpty()) {
			return createNewUser(userInfo);
		}
		
    	User found = optUser.get();
		requireActive(found);
		return linkExternalIdentity(userInfo, found);
	}
	
	private User createNewUser(ProviderUserMetadata userInfo) {
		User newUser =  userService.createUser(userInfo.name(), null, userInfo.email(), null, null, userInfo.locale(), null);

		identityRepository.save(buildExternalIdentity(userInfo, newUser));
		
		return newUser;
	}
	
	private User linkExternalIdentity(ProviderUserMetadata userInfo, User existingUser) {
		ExternalIdentity newExternalIdentity = buildExternalIdentity(userInfo, existingUser);
		
		identityRepository.save(newExternalIdentity);
		
		return existingUser;
	}
	
	private ExternalIdentity buildExternalIdentity(ProviderUserMetadata userInfo, User user) {
		return new ExternalIdentity(
				userInfo.provider(), userInfo.sub(), userInfo.email(),
				userInfo.emailVerified(), userInfo.picture(), user);
	}
	
	private void requireActive(User user) {
		if (user.getUserStatus() == UserStatus.INACTIVE) {
			throw new ApiException(UserErrorType.INACTIVE);
		}
		if (user.getUserStatus() == UserStatus.BANNED) {
			throw new ApiException(UserErrorType.BANNED);
		}
	}
}
