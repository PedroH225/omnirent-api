package br.com.omnirent.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.user.User;
import br.com.omnirent.user.UserRepository;
import lombok.AllArgsConstructor;

@Service
public class AuthenticationService implements UserDetailsService {
	@Autowired
    private ApplicationContext context;
    
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private TokenService tokenService;

    private AuthenticationManager authenticationManager;
    
    private boolean verifyExistingEmail(String email) {
		return userRepository.findExistingUserByEmail(email).isPresent();
	}
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    } 

    public Map<String, String> login(LoginDTO data){
        authenticationManager = context.getBean(AuthenticationManager.class);

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);
        var token = tokenService.generateToken((User) auth.getPrincipal());
        return Map.of("token", token);
    }

    public ResponseEntity<Object> register (RegisterDTO registerDto){
    	if (verifyExistingEmail(registerDto.email())) {
			throw new RuntimeException("Email already in use.");
		}
    	
    	String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());
        
        this.userRepository.save(fromRegisterDTO(registerDto, encryptedPassword));
        return ResponseEntity.ok().build();
    }
    
    private User fromRegisterDTO(RegisterDTO registerDTO, String encryptedPassword) {
        User user = new User();

        user.setName(registerDTO.name());
        user.setUsername(registerDTO.username());
        user.setEmail(registerDTO.email());
        user.setBirthDate(registerDTO.birthDate());
        user.setPassword(encryptedPassword);
        
        return user;
    }
}