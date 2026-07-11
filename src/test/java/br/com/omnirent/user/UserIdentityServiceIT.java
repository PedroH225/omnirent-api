package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.factory.ExternalIdentityTestFactory;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.SpringIntegrationTest;
import br.com.omnirent.security.auth.UserIdentityRepository;
import br.com.omnirent.security.auth.UserIdentityService;
import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.auth.provider.records.ProviderUserMetadata;
import br.com.omnirent.security.domain.ExternalIdentity;
import br.com.omnirent.user.domain.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
public class UserIdentityServiceIT extends SpringIntegrationTest {

	@Autowired
	private UserIdentityService userIdentityService;

	@Autowired
	private UserIdentityRepository identityRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private EntityManager entityManager;
	
	private User user;
	
	private User user2;
		
	private ProviderUserMetadata googleUser1Metadata;
	
	@BeforeEach
	void setup() {
		user = UserTestFactory.user();
		user.setUserStatus(UserStatus.ACTIVE);
		user = userRepository.save(user);
		
		user2 = UserTestFactory.user();
		user2.setUserStatus(UserStatus.ACTIVE);
		user2 = userRepository.save(user2);
		
		googleUser1Metadata = 
				ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GOOGLE);	
	}
	
	@Test
	void shouldReturnUserWhenExternalIdentityExists() {
		ExternalIdentity identity =
		        ExternalIdentityTestFactory.google(user, googleUser1Metadata);

		identityRepository.save(identity);

		entityManager.flush();
		entityManager.clear();
		
		User resolvedUser = userIdentityService.resolveUser(googleUser1Metadata);

		assertThat(resolvedUser.getId()).isEqualTo(user.getId());
	}
	
	@Test
	void shouldLinkExternalIdentityWhenUserExistsByEmail() {
		ProviderUserMetadata githubUserMetadata = ExternalIdentityTestFactory.createMetadata(user, AuthProvider.GITHUB);

		User resolvedUser = userIdentityService.resolveUser(githubUserMetadata);

		entityManager.flush();
		entityManager.clear();

		assertThat(resolvedUser.getId()).isEqualTo(user.getId());
		
		List<ExternalIdentity> linkedIdentities = entityManager.createQuery(
				"SELECT e FROM ExternalIdentity e WHERE e.user.id = :userId AND e.provider = :provider", ExternalIdentity.class)
				.setParameter("userId", user.getId())
				.setParameter("provider", AuthProvider.GITHUB)
				.getResultList();

		assertThat(linkedIdentities).hasSize(1);
		assertThat(linkedIdentities.get(0).getProviderUserId()).isEqualTo(githubUserMetadata.sub());
	}
}
