package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.config.CacheTestConfig;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.user.context.UserTakenContext;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CacheTestConfig.class)
public class UserRepositoryTest extends IntegrationTest {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserQueryRepository queryRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(UserTestFactory.user());
        user2 = userRepository.save(UserTestFactory.user());
    }
    
    @Test
    void shouldFindDetailsDTO() {
        Optional<UserDetailsDTO> userDetails1 = queryRepository.findUserDetailsById(user1.getId());
        Optional<UserDetailsDTO> userDetails2 = queryRepository.findUserDetailsById("123");

        assertThat(userDetails1).isPresent()
            .get()
            .extracting(UserDetailsDTO::getEmail)
            .isEqualTo(user1.getEmail());
            
        assertThat(userDetails2).isEmpty();
    }
    
    @Test
    void shouldFindAllUserResDTO() {
        List<UserResponseDTO> usersResDto = queryRepository.findAllUser();
        
        assertThat(usersResDto).isNotEmpty();
        assertThat(usersResDto)
            .allSatisfy(user -> {
                assertThat(user.getId()).isNotNull();
                assertThat(user.getUsername()).isNotNull();
            });    
    }
    
    @Test
    void shouldFindTakenFieldsNotId() {
        List<UserTakenContext> taken1 = queryRepository.findTakenFieldsNotId(user2.getId(), user1.getUsername(), user1.getEmail());
        List<UserTakenContext> taken2 = queryRepository.findTakenFieldsNotId(user1.getId(), user1.getUsername(), user1.getEmail());

        assertThat(taken1).isNotEmpty();
        assertThat(taken2).isEmpty();
    }
    
    @Test
    void shouldFindTakenFields() {
        List<UserTakenContext> taken1 = queryRepository.findTakenFields(user1.getUsername(), user1.getEmail());
        List<UserTakenContext> takenNotFound = queryRepository.findTakenFields("nonexistent_user", "nonexistent@email.com");

        assertThat(taken1).isNotEmpty();
        assertThat(taken1)
            .anySatisfy(ctx -> {
                assertThat(ctx).hasFieldOrPropertyWithValue("email", user1.getEmail());
            });
            
        assertThat(takenNotFound).isEmpty();
    }
    
    @Test
    void shouldUpdateUserStatusWhenCurrentStatusMatches() {
        UserStatus currentStatus = user1.getUserStatus();
        UserStatus targetStatus = currentStatus == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE;

        int affectedRows = userRepository.updateUserStatus(user1.getId(), currentStatus, targetStatus);

        assertThat(affectedRows).isEqualTo(1);

        entityManager.flush();
        entityManager.clear();

        User updatedUser = userRepository.findById(user1.getId()).orElseThrow();
        assertThat(updatedUser.getUserStatus()).isEqualTo(targetStatus);
    }

    @Test
    void shouldNotUpdateUserStatusWhenCurrentStatusIsDifferent() {
        UserStatus currentDbStatus = user1.getUserStatus();
        UserStatus wrongCurrentStatus = currentDbStatus == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE;
        UserStatus targetStatus = UserStatus.ACTIVE; 

        int affectedRows = userRepository.updateUserStatus(user1.getId(), wrongCurrentStatus, targetStatus);

        assertThat(affectedRows).isEqualTo(0);
    }

    @Test
    void shouldUpdateUserDetails() {
        String newName = "Updated Name";
        String newUsername = "updated_user";
        String newEmail = "updated@email.com";
        LocalDate newBirthDate = LocalDate.of(1995, 5, 20);

        int affectedRows = userRepository.updateUser(
            user1.getId(), newName, newUsername, newEmail, newBirthDate
        );

        assertThat(affectedRows).isEqualTo(1);

        entityManager.flush();
        entityManager.clear();

        User updatedUser = userRepository.findById(user1.getId()).orElseThrow();
        
        assertThat(updatedUser.getName()).isEqualTo(newName);
        assertThat(updatedUser.getDisplayUsername()).isEqualTo(newUsername);
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
        assertThat(updatedUser.getBirthDate()).isEqualTo(newBirthDate);
    }
    
    @Test
    void shouldReturnZeroWhenUpdatingNonExistentUser() {
        int affectedRows = userRepository.updateUser(
            "nonexistent_id", "Name", "username", "email@test.com", LocalDate.now()
        );

        assertThat(affectedRows).isEqualTo(0);
    }
}