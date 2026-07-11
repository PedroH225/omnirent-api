package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.UserErrorType;
import br.com.omnirent.factory.ExternalIdentityTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.security.auth.UserIdentityQueryRepository;
import br.com.omnirent.security.auth.UserIdentityRepository;
import br.com.omnirent.security.auth.UserIdentityService;
import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.auth.provider.records.ProviderUserMetadata;
import br.com.omnirent.security.domain.ExternalIdentity;
import br.com.omnirent.user.domain.User;

@ExtendWith(MockitoExtension.class)
public class UserIdentityServiceTest {
	
	@InjectMocks
	private UserIdentityService identityService;

	@Mock
	private UserIdentityRepository identityRepository;
	
	@Mock
	private UserIdentityQueryRepository identityQueryRepository;
	
	@Mock	
	private UserService userService;
	
	@Mock
	private UserQueryRepository userQueryRepository;
	
private User user;
	
	private User user2;
	
	private ExternalIdentity google;
	
	private ProviderUserMetadata githubUser1Metadata;
	
	private ProviderUserMetadata googleUser1Metadata;

	@BeforeEach
	void setup() {
		user = UserTestFactory.persistedUser();
		user.setUserStatus(UserStatus.ACTIVE);
		
		user2 = UserTestFactory.persistedUser();
		user2.setUserStatus(UserStatus.ACTIVE);
		
		googleUser1Metadata = ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GOOGLE);

		google = ExternalIdentityTestFactory.google(user, googleUser1Metadata);

		githubUser1Metadata = ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GITHUB);
		
	}
	
	@Test
	void shouldReturnUserWhenExternalIdentityExists() {
	    when(identityQueryRepository.findByProviderAndProviderUserId(
	            googleUser1Metadata.provider(), googleUser1Metadata.sub()))
	        .thenReturn(Optional.of(google));

	    User result = identityService.resolveUser(googleUser1Metadata);

	    assertThat(result).isSameAs(user);
	    verifyNoInteractions(userQueryRepository, userService, identityRepository);
	}
	
	@Test
	void shouldThrowExceptionWhenExternalIdentityUserIsInactive() {
	    user.setUserStatus(UserStatus.INACTIVE);

	    googleUser1Metadata = ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GOOGLE);
	    google = ExternalIdentityTestFactory.google(user, googleUser1Metadata);

	    when(identityQueryRepository.findByProviderAndProviderUserId(
	            googleUser1Metadata.provider(),
	            googleUser1Metadata.sub()))
	        .thenReturn(Optional.of(google));

	    ApiException ex = assertThrowsExactly(ApiException.class,
	            () -> identityService.resolveUser(googleUser1Metadata));

	    assertThat(ex.getErrorType())
	        .isEqualTo(UserErrorType.INACTIVE.getErrorType());

	    verifyNoInteractions(userQueryRepository, userService, identityRepository);
	}
	
}
