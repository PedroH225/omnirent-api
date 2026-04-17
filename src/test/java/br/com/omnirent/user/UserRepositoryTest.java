package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.integration.IntegrationTest;
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
        assertThat(found).satisfies(u -> {
        	assertThat(u.getId()).isNotNull();
        	assertThat(u.getCreatedAt()).isNotNull();
        	assertThat(u.getUpdatedAt()).isNotNull();
        });
        assertThat(found).satisfies(u -> {
        	assertThat(u.getName()).isEqualTo(user.getName());
        	assertThat(u.getUsername()).isEqualTo(user.getUsername());
        	assertThat(u.getEmail()).isEqualTo(user.getEmail());
        	assertThat(u.getPassword()).isEqualTo(user.getPassword());
        	assertThat(u.getBirthDate()).isEqualTo(user.getBirthDate());
        	assertThat(u.getAuthMetadata().getTokenVersion()).isEqualByComparingTo(user.getAuthMetadata().getTokenVersion());
        	assertThat(u.getAuthMetadata().getGlobalVersion()).isEqualByComparingTo(user.getAuthMetadata().getGlobalVersion());
        });
    }
    
    @Test
    void shouldSaveAllAndFindAllUser() {
        Integer globalVersion = globalConfigHolder.getGlobalTokenVersion();
        
        List<User> users = Arrays.asList(
        		new User("PedroH", "pedro226", "pedro@example.com", "password123", LocalDate.now(), 1, globalVersion),
        		new User("GuilhermeR", "guilherme123", "guilherme@gmail.com", "password123", LocalDate.now(), 1, globalVersion));
        
        List<User> savedUsers = userRepository.saveAll(users);

        List<Optional<User>> optSaved = savedUsers.stream()
        		.map(u -> userRepository.findById(u.getId()))
        		.collect(Collectors.toList());

        List<User> findSaved = Arrays.asList(optSaved.get(0).get(), optSaved.get(1).get());
        
        assertThat(findSaved).isNotNull();
        assertThat(findSaved).allSatisfy(u -> {
        	assertThat(u.getId()).isNotNull();
        	assertThat(u.getCreatedAt()).isNotNull();
        	assertThat(u.getUpdatedAt()).isNotNull();
        });
        
        for (int i = 0; i < findSaved.size(); i++) {
			User u = findSaved.get(i);
			User target = users.get(i);
			
			assertThat(u.getName()).isEqualTo(target.getName());
		    assertThat(u.getUsername()).isEqualTo(target.getUsername());
		    assertThat(u.getEmail()).isEqualTo(target.getEmail());
		    assertThat(u.getPassword()).isEqualTo(target.getPassword());
		    assertThat(u.getBirthDate()).isEqualTo(target.getBirthDate());

		    assertThat(u.getAuthMetadata().getTokenVersion())
		        .isEqualTo(target.getAuthMetadata().getTokenVersion());

		    assertThat(u.getAuthMetadata().getGlobalVersion())
		        .isEqualTo(target.getAuthMetadata().getGlobalVersion());
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