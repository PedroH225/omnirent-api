package br.com.omnirent.user;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.UserEnums;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.event.SpringDomainEventPublisher;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.apptype.ConcurrencyErrorType;
import br.com.omnirent.exception.domain.apptype.UserErrorType;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.context.ChangeUserStatusContext;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import br.com.omnirent.user.event.UserStatusChangeEvent;
import br.com.omnirent.user.event.UserUpdatedEvent;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {

	private UserMapper userMapper;

	private UserRepository userRepository;
	
	private UserQueryRepository queryRepository;
	
	private CurrentUserProvider currentUserProvider;
	
	private UserValidationService validationService;
	
	private UserAutorizationService autorizationService;
	
	private SpringDomainEventPublisher eventPublisher;
		
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
		        userId, updatedUser.getId(),
		        userMapper.toAuditSnapshot(updatedUser),
		        Instant.now()));

		return updatedUser;
	}

	@Transactional
	public void changeUserStatus() {
		String userId = currentUserProvider.currentUserId();
		ChangeUserStatusContext context = queryRepository.getUserStatusChangeContext(userId)
				.orElseThrow(() -> new ApiException(UserErrorType.NOT_FOUND));
		
		autorizationService.requireNotBanned(context.currentUserStatus());
		
		UserStatus newStatus = context.currentUserStatus() == UserStatus.ACTIVE ?
				UserStatus.INACTIVE : UserStatus.ACTIVE;
		
		int updated =
				userRepository.updateUserStatus(userId, context.currentUserStatus(), newStatus);
		
		if (updated == 0) {
			throw new ApiException(ConcurrencyErrorType.OPTMISTIC_LOCK);
		}
		
		eventPublisher.publish(
			    new UserStatusChangeEvent(
			        userId, userId, newStatus, context.email(),
			        context.username(), Locale.forLanguageTag(context.locale())
			        ));	
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
}
