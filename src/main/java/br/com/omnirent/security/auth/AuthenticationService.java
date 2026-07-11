package br.com.omnirent.security.auth;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.security.TokenService;
import br.com.omnirent.security.auth.provider.AuthProvider;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.security.dto.RegisterDTO;
import br.com.omnirent.security.event.UserLoggedInEvent;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.UserQueryRepository;
import br.com.omnirent.user.UserService;
import br.com.omnirent.user.UserValidationService;
import br.com.omnirent.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthenticationService implements UserDetailsService {
	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserQueryRepository queryRepository;
	
	@Autowired
	private UserService userService;

	@Autowired
	private TokenService tokenService;

	private AuthenticationManager authenticationManager;

	@Autowired
	private UserMapper mapper;

	@Autowired
	private UserValidationService validationService;

	@Autowired
	private SpringDomainEventPublisher eventPublisher;

	@Autowired
	private Clock clock;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User context = queryRepository.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException(email));
		
		requireActive(context);
		return mapper.toAuthUser(context);
	}

	public Map<String, String> login(LoginDTO data, HttpServletRequest request) {
		try {
			authenticationManager = context.getBean(AuthenticationManager.class);

			var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
			var auth = this.authenticationManager.authenticate(usernamePassword);

			var user = (AuthenticatedUser) auth.getPrincipal();

			var token = tokenService.generateToken(user);

			String ip = extractIp(request);
			String userAgent = request.getHeader("User-Agent");

			eventPublisher.publish(new UserLoggedInEvent(
							user.getId(), ip, userAgent, AuthProvider.LOGIN_PASSWORD,
							true, Instant.now(clock)));

			return Map.of("token", token);
		} 
		catch (InternalAuthenticationServiceException e) {
		    if (e.getCause() instanceof ApiException ae) {
		        throw ae;
		    }

		    throw new ApiException(AuthenticationErrorType.AUTHENTICATION_SERVICE_ERROR);
		}
		catch (BadCredentialsException e) {
			throw new ApiException(AuthenticationErrorType.INVALID_CREDENTIALS);
		}
	}

	public ResponseEntity<Object> register(RegisterDTO registerDto, HttpServletRequest request) {
		validationService.validateTakenFields(null, registerDto);
		validationService.validatePasswordMatch(registerDto.password(), registerDto.repeatedPassword());

		String locale = request.getHeader("Accept-Language");
		String timezone = request.getHeader("Timezone");

		String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());
		
		userService.createUser(
				registerDto.name(), registerDto.username(),registerDto.email(),
				encryptedPassword, registerDto.birthDate(), 
				locale, timezone);

		return ResponseEntity.ok().build();
	}

	private String extractIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");

		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}

		return request.getRemoteAddr();
	}
	
	private void requireActive(User user) {
		if (user.getUserStatus() != UserStatus.ACTIVE) {
			log.debug("{} user tried to login, id: {}",
					user.getUserStatus(),
					user.getId());
			throw new ApiException(AuthenticationErrorType.INVALID_CREDENTIALS);
		}
	}
}