package br.com.omnirent.security.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.omnirent.security.domain.ExternalIdentity;

@Repository
public interface UserIdentityRepository extends JpaRepository<ExternalIdentity, String>{

}
