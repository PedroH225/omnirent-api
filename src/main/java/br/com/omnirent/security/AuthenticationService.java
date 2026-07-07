package br.com.omnirent.security;

import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.AuthenticationErrorType;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.security.dto.RegisterDTO;
import br.com.omnirent.security.event.UserLoggedInEvent;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.UserMapper;
import br.com.omnirent.user.UserQueryRepository;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.UserValidationService;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class AuthenticationService implements UserDetailsService {
	@Autowired
	private ApplicationContext context;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserQueryRepository queryRepository;

	@Autowired
	private TokenService tokenService;

	private AuthenticationManager authenticationManager;

	@Autowired
	private UserMapper mapper;

	@Autowired
	private GlobalConfigHolder globalConfigHolder;

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
							user.getId(), ip, userAgent, true, Instant.now()));

			return Map.of("token", token);
		} catch (BadCredentialsException e) {
			throw new ApiException(AuthenticationErrorType.INVALID_CREDENTIALS);
		}
	}

	public ResponseEntity<Object> register(RegisterDTO registerDto) {
		validationService.validateTakenFields(null, registerDto);
		validationService.validatePasswordMatch(registerDto.password(), registerDto.repeatedPassword());

		Locale userLocale = LocaleContextHolder.getLocale();

		String encryptedPassword = new BCryptPasswordEncoder().encode(registerDto.password());

		User persistedUser = this.userRepository.save(fromRegisterDTO(registerDto, encryptedPassword, userLocale));

		eventPublisher.publish(new UserRegisteredEvent(AuditAction.USER_REGISTERED, persistedUser.getId(),
				mapper.toAuditSnapshot(persistedUser), Instant.now(clock), userLocale));

		return ResponseEntity.ok().build();
	}

	private User fromRegisterDTO(RegisterDTO registerDTO, String encryptedPassword, Locale locale) {
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
		user.setLocale(locale.toLanguageTag());

		return user;
	}

	private String extractIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");

		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}

		return request.getRemoteAddr();
	}
}