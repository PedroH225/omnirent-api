package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

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
}