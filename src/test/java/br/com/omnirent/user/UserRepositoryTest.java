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
		
	private static List<User> generateUsers() {
		RandomStringUtils randomStringUtils = RandomStringUtils.secure();
		List<User> tempList = new ArrayList<User>();

		for (int i = 0; i < 2; i++) {
			String string = randomStringUtils.nextAlphabetic(10);
			String email = string + "@email.com";
			tempList.add(new User(string, string, email, string, LocalDate.now(), 1, 1));
		}
		return tempList;
	}
    
    @Test
    void shouldFindDetailsDTO() {
		User user = new User("testuser1", "testuser1", "test1@example.com", "password123", LocalDate.now(), 1, globalConfigHolder.getGlobalTokenVersion());
        User saved = userRepository.save(user);

    	Optional<UserDetailsDTO> userDetails1 = userRepository.findUserDetailsById(saved.getId());
    	Optional<UserDetailsDTO> userDetails2 = userRepository.findUserDetailsById("123");

    	assertThat(userDetails1).isPresent()
    	.get()
    	.extracting(UserDetailsDTO::getEmail)
    	.isEqualTo(saved.getEmail());
    	assertThat(userDetails2).isEmpty();
    }
    
    @Test
    void shouldFindAllUserResDTO() {
    	userRepository.saveAll(generateUsers());
    	
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
    	List<User> res = userRepository.saveAll(generateUsers());
    	    	
    	Optional<User> find1 = userRepository.findByEmailAndIdNot(res.get(0).getEmail(), res.get(1).getId());
    	Optional<User> find2 = userRepository.findByEmailAndIdNot(res.get(0).getEmail(), res.get(0).getId());

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
    	List<User> res = userRepository.saveAll(generateUsers());
    	    	
    	Optional<User> find1 = userRepository.findExistingUserByEmail(res.get(1).getEmail());

		assertThat(find1).isPresent();
		assertThat(find1.get())
	    .satisfies(u -> {
	        assertThat(u.getId()).isNotNull();
	        assertThat(u.getUsername()).isNotNull();
	        assertThat(u.getEmail()).isNotNull();
	        assertThat(u.getBirthDate()).isNotNull();
	    });
	}
}