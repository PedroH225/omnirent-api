package br.com.omnirent.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import br.com.omnirent.security.context.LoginContext;
import br.com.omnirent.user.context.ChangeUserStatusContext;
import br.com.omnirent.user.context.UserNotificationData;
import br.com.omnirent.user.context.UserTakenContext;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserResponseDTO;

public interface UserQueryRepository extends Repository<User, String> {
	
	@Query("""
			SELECT new br.com.omnirent.user.context.UserNotificationData(u.id, u.username,
			u.email, u.locale)
			FROM User u
			WHERE u.id = :id
			""")
	Optional<UserNotificationData> findNotificationData(@Param("id")String userId);
	
	@Query("""
			SELECT new br.com.omnirent.security.context.LoginContext(u.id, u.email, u.password,
			u.authMetadata.globalVersion, u.authMetadata.tokenVersion) 
			FROM User u 
			WHERE u.email = :email
			""")
	Optional<LoginContext> findByEmail(String email);
	
	@Query("SELECT u.authMetadata FROM User u WHERE u.id = :id")
	AuthMetadata findTokenVersionById(@Param("id") String id);
	
	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.id = :id AND u.userStatus=ACTIVE")
	Boolean verifyUser(@Param("id")String userId);
	
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
	
	@Query("""
			SELECT new br.com.omnirent.user.context.UserTakenContext(u.username, u.email)
			FROM User u WHERE u.username = :username OR u.email = :email
			""")
	List<UserTakenContext> findTakenFields(String username, String email);
	
	@Query("""
			SELECT new br.com.omnirent.user.context.UserTakenContext(u.username, u.email)
			FROM User u WHERE u.id != :id AND (u.username = :username OR u.email = :email)
			""")
	List<UserTakenContext> findTakenFieldsNotId(String id, String username, String email);
	
	@Query("""
			SELECT new br.com.omnirent.user.context.ChangeUserStatusContext(u.id, u.userStatus, 
			u.email, u.username, u.locale)
			FROM User u WHERE u.id = :id
			""")
	Optional<ChangeUserStatusContext> getUserStatusChangeContext(@Param("id") String userId); 
}
