package br.com.omnirent.config;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.omnirent.config.global.AdminProperties;
import br.com.omnirent.config.global.GlobalConfigHolder;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.domain.RoleNotFoundException;
import br.com.omnirent.security.auth.RoleRepository;
import br.com.omnirent.security.domain.Role;
import br.com.omnirent.user.UserQueryRepository;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    
    private final RoleRepository roleRepository;
    
    private final UserQueryRepository queryRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private final AppProperties appProperties;

    private final AdminProperties adminProperties;
    
    private final GlobalConfigHolder globalConfigHolder;

    @Override
    public void run(String... args) {
        if (queryRepository.findByEmail(adminProperties.email()).isPresent()) {
            return;
        }

        User admin = new User(
                null,
                adminProperties.name(),
                adminProperties.username(),
                adminProperties.email(),
                passwordEncoder.encode(adminProperties.password()),
                null,
                1,
                globalConfigHolder.getGlobalTokenVersion()
        );
        
        admin.setLocale(appProperties.locale());
        admin.setTimezone(appProperties.timezone());
        
        Role roleUser = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));
		admin.getRoles().add(roleUser);
		
		Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
				.orElseThrow(() -> new RoleNotFoundException("ROLE_ADMIN"));
		admin.getRoles().add(roleAdmin);

        userRepository.save(admin);
    }
}