package br.com.omnirent.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import br.com.omnirent.user.context.UserTakenContext;
import br.com.omnirent.user.domain.User;

public interface UserQueryRepository extends Repository<User, String> {

	@Query("""
			SELECT new br.com.omnirent.user.context.UserTakenContext(u.username, u.email)
			FROM User u WHERE u.username = :username OR u.email = :email
			""")
	List<UserTakenContext> findTakenFields(String username, String email);
}
