package br.com.omnirent.user;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.user.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
	Optional<User> findByEmail(String emal);
	
	@Modifying
	@Query("""
			UPDATE User u SET u.userStatus = :targetStatus
			WHERE u.id = :id AND u.userStatus = :currentStatus
			""")
	int updateUserStatus(String id, UserStatus currentStatus, UserStatus targetStatus);
	
	@Modifying
	@Query("""
			UPDATE User u SET u.name = :name, u.username = :username, u.email = :email,
			u.birthDate = :birthDate
			WHERE u.id = :id
			""")
	int updateUser(String id, String name, String username, String email, LocalDate birthDate);
	
}
