package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import br.com.omnirent.config.CacheTestConfig;
import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.factory.UserTestFactory;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import jakarta.transaction.Transactional;

@Transactional
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(CacheTestConfig.class)
public class UserRepositoryTest extends IntegrationTest {

	@Autowired
    private UserRepository userRepository;
	
	private User user1;
	private User user2;

	@BeforeEach
	void setUp() {
		user1 = userRepository.save(UserTestFactory.user());
	    user2 = userRepository.save(UserTestFactory.user());
	}
    
    @Test
    void shouldFindDetailsDTO() {
    	Optional<UserDetailsDTO> userDetails1 = userRepository.findUserDetailsById(user1.getId());
    	Optional<UserDetailsDTO> userDetails2 = userRepository.findUserDetailsById("123");

    	assertThat(userDetails1).isPresent()
    	.get()
    	.extracting(UserDetailsDTO::getEmail)
    	.isEqualTo(user1.getEmail());
    	assertThat(userDetails2).isEmpty();
    }
    
    @Test
    void shouldFindAllUserResDTO() {
		List<UserResponseDTO> usersResDto = userRepository.findAllUser();
		
		assertThat(usersResDto).isNotEmpty();
		assertThat(usersResDto)
	    .allSatisfy(user -> {
	        assertThat(user.getId()).isNotNull();
	        assertThat(user.getUsername()).isNotNull();
	        });    
	}
    
    @Test
    void shouldFindByEmailNotId() {    	    	
    	Optional<User> find1 = userRepository.findByEmailAndIdNot(user1.getEmail(), user2.getId());
    	Optional<User> find2 = userRepository.findByEmailAndIdNot(user1.getEmail(), user1.getId());

		assertThat(find1).isPresent();
		assertThat(find1.get())
	    .satisfies(u -> {
	        assertThat(u.getId()).isNotNull();
	        assertThat(u.getUsername()).isNotNull();
	        assertThat(u.getEmail()).isNotNull();
	        assertThat(u.getBirthDate()).isNotNull();
	    });
		assertThat(find2).isEmpty();   
	}
    
    @Test
    void shouldFindByExistingUserByEmail() {
    	Optional<User> find = userRepository.findExistingUserByEmail(user1.getEmail());

		assertThat(find).isPresent();
		assertThat(find.get())
	    .satisfies(u -> {
	        assertThat(u.getId()).isNotNull();
	        assertThat(u.getUsername()).isNotNull();
	        assertThat(u.getEmail()).isNotNull();
	        assertThat(u.getBirthDate()).isNotNull();
	    });
	}
}