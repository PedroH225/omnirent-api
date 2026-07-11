package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
		
	private ProviderUserMetadata googleUser1Metadata;

	@BeforeEach
	void setup() {
		user = UserTestFactory.persistedUser();
		user.setUserStatus(UserStatus.ACTIVE);
		
		user2 = UserTestFactory.persistedUser();
		user2.setUserStatus(UserStatus.ACTIVE);
		
		googleUser1Metadata = ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GOOGLE);

		google = ExternalIdentityTestFactory.google(user, googleUser1Metadata);		
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

	    assertThat(ex.getErrorCode())
	        .isEqualTo(UserErrorType.INACTIVE.getErrorCode());

	    verifyNoInteractions(userQueryRepository, userService, identityRepository);
	}
	
	@Test
	void shouldThrowExceptionWhenExternalIdentityUserIsBanned() {
		user.setUserStatus(UserStatus.BANNED);
		
		googleUser1Metadata = ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GOOGLE);
	    google = ExternalIdentityTestFactory.google(user, googleUser1Metadata);
	    
		when(identityQueryRepository.findByProviderAndProviderUserId(
				googleUser1Metadata.provider(), googleUser1Metadata.sub()))
				.thenReturn(Optional.of(google));

		ApiException ex = assertThrowsExactly(ApiException.class, 
				() -> identityService.resolveUser(googleUser1Metadata));

		assertThat(ex.getErrorCode()).isEqualTo(UserErrorType.BANNED.getErrorCode());
		verifyNoInteractions(userQueryRepository, userService, identityRepository);
	}
	
	@Test
	void shouldCreateNewUserAndExternalIdentityWhenUserDoesNotExist() {
	    ProviderUserMetadata googleUser2Metadata =
	            ExternalIdentityTestFactory.createMetadata(user2, AuthProvider.GOOGLE);

	    when(identityQueryRepository.findByProviderAndProviderUserId(
	            googleUser2Metadata.provider(), googleUser2Metadata.sub()))
	            .thenReturn(Optional.empty());

	    when(userQueryRepository.findByEmail(googleUser2Metadata.email()))
	            .thenReturn(Optional.empty());

	    when(userService.createUser(
	            googleUser2Metadata.name(), null,
	            googleUser2Metadata.email(), null,
	            null, googleUser2Metadata.locale(),
	            null)).thenReturn(user2);

	    User result = identityService.resolveUser(googleUser2Metadata);

	    assertThat(result).isSameAs(user2);

	    verify(userService).createUser(
	            googleUser2Metadata.name(), null,
	            googleUser2Metadata.email(), null,
	            null, googleUser2Metadata.locale(),
	            null);
	    
	    ArgumentCaptor<ExternalIdentity> captor =
	            ArgumentCaptor.forClass(ExternalIdentity.class);

	    verify(identityRepository).save(captor.capture());

	    ExternalIdentity savedIdentity = captor.getValue();

	    assertThat(savedIdentity.getProvider())
	            .isEqualTo(googleUser2Metadata.provider());

	    assertThat(savedIdentity.getProviderUserId())
	            .isEqualTo(googleUser2Metadata.sub());

	    assertThat(savedIdentity.getUser())
	            .isSameAs(user2);
	}
	
	@Test
	void shouldLinkExternalIdentityWhenUserExistsByEmail() {
		ProviderUserMetadata existingUserGithubMetadata = 
				ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GITHUB);
		
		when(identityQueryRepository.findByProviderAndProviderUserId(existingUserGithubMetadata.provider(), existingUserGithubMetadata.sub()))
				.thenReturn(Optional.empty());
		when(userQueryRepository.findByEmail(existingUserGithubMetadata.email()))
				.thenReturn(Optional.of(user));

		User result = identityService.resolveUser(existingUserGithubMetadata);

		assertThat(result).isEqualTo(user);
		
		ArgumentCaptor<ExternalIdentity> captor = ArgumentCaptor.forClass(ExternalIdentity.class);
		verify(identityRepository).save(captor.capture());
		
		ExternalIdentity savedIdentity = captor.getValue();
		assertThat(savedIdentity.getProvider()).isEqualTo(existingUserGithubMetadata.provider());
		assertThat(savedIdentity.getProviderUserId()).isEqualTo(existingUserGithubMetadata.sub());
		assertThat(savedIdentity.getUser()).isEqualTo(user);
		
		verifyNoInteractions(userService);
	}
	
	@Test
	void shouldThrowExceptionWhenExistingEmailUserIsInactive() {
		user.setUserStatus(UserStatus.INACTIVE);
		
		ProviderUserMetadata googleUser1Metadata =
		        ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GOOGLE);
		
		when(identityQueryRepository.findByProviderAndProviderUserId(googleUser1Metadata.provider(), googleUser1Metadata.sub()))
				.thenReturn(Optional.empty());
		when(userQueryRepository.findByEmail(googleUser1Metadata.email()))
				.thenReturn(Optional.of(user));

		ApiException ex = assertThrowsExactly(ApiException.class, () -> identityService.resolveUser(googleUser1Metadata));

		assertThat(ex.getErrorCode()).isEqualTo(UserErrorType.INACTIVE.getErrorCode());
		verifyNoInteractions(identityRepository, userService);
	}
	
	@Test
	void shouldThrowExceptionWhenExistingEmailUserIsBanned() {
		user.setUserStatus(UserStatus.BANNED);
		
		ProviderUserMetadata googleUser1Metadata =
		        ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GOOGLE);
		
		when(identityQueryRepository.findByProviderAndProviderUserId(googleUser1Metadata.provider(), googleUser1Metadata.sub()))
				.thenReturn(Optional.empty());
		when(userQueryRepository.findByEmail(googleUser1Metadata.email()))
				.thenReturn(Optional.of(user));

		ApiException ex = assertThrowsExactly(ApiException.class, () -> identityService.resolveUser(googleUser1Metadata));

		assertThat(ex.getErrorCode()).isEqualTo(UserErrorType.BANNED.getErrorCode());
		verifyNoInteractions(identityRepository, userService);
	}
	
}
