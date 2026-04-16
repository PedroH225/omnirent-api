package br.com.omnirent.security;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.exception.domain.EmailInUseException;
import br.com.omnirent.exception.domain.FailedLoginException;
import br.com.omnirent.security.context.LoginContext;
import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.security.dto.RegisterDTO;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;

@Service
public class AuthenticationService implements UserDetailsService {
	@Autowired
    private ApplicationContext context;
    
	@Autowired
    private UserRepository userRepository;
	
	@Autowired
    private TokenService tokenService;

    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserMapper mapper;
    
    @Autowired
    private GlobalConfigHolder globalConfigHolder;
    
    private boolean verifyExistingEmail(String email) {
		return userRepository.findExistingUserByEmail(email).isPresent();
	}
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<LoginContext> optUser = userRepository.findByEmail(email);
        
        if (optUser.isEmpty()) {
			throw new UsernameNotFoundException(email);
		}
        
        return mapper.toAuthUser(optUser.get());
    } 

    public Map<String, String> login(LoginDTO data){
    	try {
        authenticationManager = context.getBean(AuthenticationManager.class);

        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        	 var auth = this.authenticationManager.authenticate(usernamePassword);
             var token = tokenService.generateToken((User) auth.getPrincipal());
             return Map.of("token", token);
		} catch (BadCredentialsException e) {
			throw new FailedLoginException();
		}
    }

    public ResponseEntity<Object> register (RegisterDTO registerDto){
    	String email = registerDto.email();
    	if (verifyExistingEmail(email)) {
			throw new EmailInUseException(email);
		}
    	
    	String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());
        
        this.userRepository.save(fromRegisterDTO(registerDto, encryptedPassword));
        return ResponseEntity.ok().build();
    }
    
    private User fromRegisterDTO(RegisterDTO registerDTO, String encryptedPassword) {
        User user = new User();

        AuthMetadata authMetadata = new AuthMetadata();
        authMetadata.setTokenVersion(1);
        authMetadata.setGlobalVersion(globalConfigHolder.getGlobalTokenVersion());
        
        user.setAuthMetadata(authMetadata);
        user.setName(registerDTO.name());
        user.setUsername(registerDTO.username());
        user.setEmail(registerDTO.email());
        user.setBirthDate(registerDTO.birthDate());
        user.setPassword(encryptedPassword);
        
        return user;
    }
}