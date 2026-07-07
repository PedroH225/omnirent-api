package br.com.omnirent.security.auth;


import java.util.Optional;

import org.springframework.data.repository.Repository;

import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.domain.ExternalIdentity;

public interface UserIdentityQueryRepository extends Repository<ExternalIdentity, String>{

	Optional<ExternalIdentity> findByProviderAndProviderUserId(AuthProvider provider, String providerUserId);

}
