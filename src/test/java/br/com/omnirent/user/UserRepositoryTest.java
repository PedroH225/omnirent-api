package br.com.omnirent.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        User user = new User();
        
        AuthMetadata authMetadata = new AuthMetadata();
        authMetadata.setTokenVersion(1);
        authMetadata.setGlobalVersion(globalConfigHolder.getGlobalTokenVersion());
        
        user.setAuthMetadata(authMetadata);
        user.setName("PedroH");
        user.setUsername("pedro225");
        user.setEmail("pedro@gmail.com");
        user.setBirthDate(LocalDate.now());
        user.setPassword("password123");
        
        User saved = userRepository.save(user);

        User found = userRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("PedroH");
    }
}