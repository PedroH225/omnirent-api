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
import br.com.omnirent.user.RegisterDTO;
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


}