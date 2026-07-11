package br.com.omnirent.user;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.audit.AuditAction;
import br.com.omnirent.common.enums.UserEnums;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.common.formatter.IdentityGeneratorUtil;
import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.config.properties.AppLocale;
import br.com.omnirent.config.properties.AppProperties;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.RoleNotFoundException;
import br.com.omnirent.exception.domain.apptype.ConcurrencyErrorType;
import br.com.omnirent.exception.domain.apptype.UserErrorType;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.security.auth.RoleRepository;
import br.com.omnirent.security.domain.Role;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.context.ChangeUserStatusContext;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import br.com.omnirent.user.event.UserStatusChangeEvent;
import br.com.omnirent.user.event.UserUpdatedEvent;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {

	private UserMapper userMapper;

	private UserRepository userRepository;
	
	private UserQueryRepository queryRepository;
	
	private final RoleRepository roleRepository;
	
	private final GlobalConfigHolder globalConfigHolder;
	
	private CurrentUserProvider currentUserProvider;
	
	private UserValidationService validationService;
	
	private UserAutorizationService autorizationService;
	
	private SpringDomainEventPublisher eventPublisher;
	
	private Clock clock;
	
	private AppProperties appProperties;
		
	public void requireExistence(String userId) {
		if (!queryRepository.verifyUser(userId)) {
			throw new ApiException(UserErrorType.NOT_FOUND);
		}
	}
	
	public User getUserReference(String userId) {
		return userRepository.getReferenceById(userId);
	}
	
	public User getValidReference(String userId) {
		requireExistence(userId);
		return getUserReference(userId);
	}
	
	public UserDetailsDTO getUserDetailsById() {
		String userId = currentUserProvider.currentUserId();
		UserDetailsDTO result = queryRepository.findUserDetailsById(userId)
				.orElseThrow(() -> new ApiException(UserErrorType.NOT_FOUND));
		
		return userMapper.localize(result);
	}

	public List<UserResponseDTO> findAll() {
		return queryRepository.findAllUser();
	}
	
	public User createUser(
			String name, String username, String email, String password,
			LocalDate birthDate, String locale, String timezone) {
		String validName = StringUtils.isNotBlank(name) 
				? name : IdentityGeneratorUtil.generateNameFromEmail(email);
		
		String validUsername = StringUtils.isNotBlank(username) 
				? username : IdentityGeneratorUtil.generateUniqueUsername(email);
		
		String validLocale = resolveLocale(locale);
		
		String validTimezone = resolveTimezone(timezone);
		
		while (userRepository.existsByUsername(validUsername)) {
			validUsername = IdentityGeneratorUtil.generateUniqueUsername(email);
		}
		
		Integer currentVersion = globalConfigHolder.getGlobalTokenVersion();
		User newUser = new User(
				null, validName, validUsername, email, password,
				birthDate, 1, currentVersion);
		
		Role role = roleRepository.findByName("ROLE_USER")
				.orElseThrow(() -> new RoleNotFoundException("ROLE_USER"));
		newUser.getRoles().add(role);
		
		newUser.setLocale(validLocale);
		newUser.setTimezone(validTimezone);
		
		User persistedUser = userRepository.save(newUser);

		eventPublisher.publish(new UserRegisteredEvent(AuditAction.USER_REGISTERED, persistedUser.getId(),
				userMapper.toAuditSnapshot(persistedUser), Instant.now(clock), Locale.forLanguageTag(validLocale)));

		return persistedUser;
	}

	@Transactional
	public UserDetailsDTO update(UserRequestDTO userDTO) {
		String userId = currentUserProvider.currentUserId();
		requireExistence(userId);
		
		validationService.validateTakenFields(userId, userDTO);
		
		int updated = userRepository.updateUser(userId, userDTO.name(), userDTO.username(),
				userDTO.email(), userDTO.birthDate());
				
		if (updated == 0) {
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
		}
		
		UserDetailsDTO updatedUser = getUserDetailsById();

		eventPublisher.publish(
		    new UserUpdatedEvent(
		    	AuditAction.USER_UPDATED, userId, updatedUser.getId(),
		        userMapper.toAuditSnapshot(updatedUser),
		        Instant.now(clock)));

		return updatedUser;
	}

	@Transactional
	public void changeUserStatus() {
		String userId = currentUserProvider.currentUserId();
		ChangeUserStatusContext context = queryRepository.getUserStatusChangeContext(userId)
				.orElseThrow(() -> new ApiException(UserErrorType.NOT_FOUND));
		
		autorizationService.requireNotBanned(context.currentUserStatus());
		
		UserStatus currentStatus = context.currentUserStatus();
		UserStatus newStatus = currentStatus == UserStatus.ACTIVE ?
				UserStatus.INACTIVE : UserStatus.ACTIVE;
		
		int updated =
				userRepository.updateUserStatus(userId, currentStatus, newStatus);
		
		if (updated == 0) {
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
		}
		
		eventPublisher.publish(
			    new UserStatusChangeEvent(
			    	AuditAction.USER_STATUS_CHANGED, userId, userId,
			        userMapper.toStatusChangeAuditSnapshot(newStatus),
			        userMapper.toStatusChangeAuditSnapshot(currentStatus),
			        Instant.now(clock)));	
	}
	
	public UserEnums getEnums() {
		return userMapper.getLocalizedEnums();
	}
	
	@Cacheable(value = "tokenVersion", key = "#userId")
	public AuthMetadata getTokenVersion(String userId) {
	    AuthMetadata authMetadata = queryRepository.findTokenVersionById(userId);
	    
	    return authMetadata;
	}
	
	@CacheEvict(value = "tokenVersion", key = "#user.id")
	public User saveUpdatingToken(User user) {
		AuthMetadata authMetadata = user.getAuthMetadata();
		Integer currentTokenVer = authMetadata.getTokenVersion();
		authMetadata.setTokenVersion(currentTokenVer == null ? 1 : currentTokenVer + 1);
	    return userRepository.save(user);
	}
	
	private String resolveTimezone(String timezone) {
		String defaultTimezone = appProperties.timezone();
	    if (StringUtils.isBlank(timezone)) {
	        return defaultTimezone;
	    }

	    try {
	        ZoneId.of(timezone);
	        return timezone;
	    } catch (DateTimeException e) {
	        return defaultTimezone;
	    }
	}

	private String resolveLocale(String locale) {
	    return !StringUtils.isBlank(locale) 
	    		&& AppLocale.SUPPORTED_LOCALES.contains(locale)
	    		? locale : appProperties.locale();
	}
}
