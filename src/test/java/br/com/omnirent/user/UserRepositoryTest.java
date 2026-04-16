package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.useRepresentation;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.g;

import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.integration.IntegrationTest;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;

@SpringBootTest
class UserRepositoryTest extends IntegrationTest {
	
	@Autowired
	GlobalConfigHolder globalConfigHolder;

	@Autowired
    private UserRepository userRepository;
	
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
        		new User("PedroH", "pedro226", "pedro@example.com", "password123", LocalDate.now(), 1, globalVersion).activate(),
        		new User("GuilhermeR", "guilherme123", "guilherme@gmail.com", "password123", LocalDate.now(), 1, globalVersion));
        
        List<User> savedUsers = userRepository.saveAll(users);

        List<Optional<User>> findSaved = savedUsers.stream()
        		.map(u -> userRepository.findById(u.getId()))
        		.collect(Collectors.toList());

        
        for (Optional<User> optUser : findSaved) {
        	assertThat(optUser.isPresent());
            assertThat(optUser.get().getId()).isNotNull();
		}        
    }
}