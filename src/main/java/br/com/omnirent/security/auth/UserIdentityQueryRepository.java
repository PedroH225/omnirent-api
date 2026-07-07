package br.com.omnirent.security.auth;


import org.springframework.data.repository.Repository;

import br.com.omnirent.security.domain.ExternalIdentity;

public interface UserIdentityQueryRepository extends Repository<ExternalIdentity, String>{

}
