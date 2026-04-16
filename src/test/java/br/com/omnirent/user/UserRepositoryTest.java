package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.useRepresentation;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.g;

import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserResponseDTO;

@SpringBootTest
class UserRepositoryTest extends IntegrationTest {
	
	@Autowired
	GlobalConfigHolder globalConfigHolder;

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
    void shouldSaveAndFindUser() {
        Integer globalVersion = globalConfigHolder.getGlobalTokenVersion();
        
        User user = new User("PedroH", "pedro225", "pedro@gmail.com", "password123",
        		LocalDate.now(), 1, globalVersion);
        
        User saved = userRepository.save(user);

        User found = userRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isNotNull();
        assertThat(found.getName()).isEqualTo("PedroH");
    }
    
    @Test
    void shouldSaveAllAndFindAllUser() {
        Integer globalVersion = globalConfigHolder.getGlobalTokenVersion();
        
        List<User> users = Arrays.asList(
        		new User("PedroH", "pedro226", "pedro@example.com", "password123", LocalDate.now(), 1, globalVersion),
        		new User("GuilhermeR", "guilherme123", "guilherme@gmail.com", "password123", LocalDate.now(), 1, globalVersion));
        
        List<User> savedUsers = userRepository.saveAll(users);

        List<Optional<User>> findSaved = savedUsers.stream()
        		.map(u -> userRepository.findById(u.getId()))
        		.collect(Collectors.toList());

        
        for (Optional<User> optUser : findSaved) {
        	assertThat(optUser).isPresent();
            assertThat(optUser.get().getId()).isNotNull();
		}        
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