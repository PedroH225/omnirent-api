package br.com.omnirent.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

	Optional<UserDetails> findByEmail(String email);
	
	Optional<User> findByEmailAndIdNot(String email, String id);
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
	Optional<User> findExistingUserByEmail(String email);
}
