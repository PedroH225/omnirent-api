package br.com.omnirent.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserResponseDTO;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	@Query("""
			SELECT new br.com.omnirent.user.dto.UserDetailsDTO(u.id, u.name, u.username, 
			u.email, u.birthDate, u.userStatus)
			FROM User u WHERE u.id = :id
			""")
	Optional<UserDetailsDTO> findUserDetailsById(String id);

	@Query("""
			SELECT new br.com.omnirent.user.dto.UserResponseDTO(u.id, u.username) FROM User u
			""")
	List<UserResponseDTO> findAllUser();
	
	Optional<UserDetails> findByEmail(String email);
	
	Optional<User> findByEmailAndIdNot(String email, String id);
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
	Optional<User> findExistingUserByEmail(String email);
	
	@Query("SELECT u.authMetadata FROM User u WHERE u.id = :id")
	AuthMetadata findTokenVersionById(@Param("id") String id);
	
	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :id AND u.userStatus=ACTIVE")
	Boolean verifyUser(@Param("id")String userId);
}
