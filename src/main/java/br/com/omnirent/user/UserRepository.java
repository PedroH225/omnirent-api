package br.com.omnirent.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.security.context.LoginContext;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	@Modifying
	@Query("""
			UPDATE User u SET u.userStatus = :targetStatus
			WHERE u.id = :id AND u.userStatus = :currentStatus
			""")
	int updateUserStatus(String id, UserStatus currentStatus, UserStatus targetStatus);
	
}
