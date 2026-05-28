package br.com.omnirent.user;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.omnirent.common.enums.UserEnums;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.exception.domain.ConcurrencyErrorType;
import br.com.omnirent.exception.domain.UserErrorType;
import br.com.omnirent.security.CurrentUserProvider;
import br.com.omnirent.user.context.ChangeUserStatusContext;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import jakarta.transaction.Transactional;
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
		
	public void requireExistence(String userId) {
		if (!userRepository.verifyUser(userId)) {
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
		
	public User findById(String id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ApiException(UserErrorType.NOT_FOUND));
	}
	
	public UserDetailsDTO getUserDetailsById() {
		String userId = currentUserProvider.currentUserId();
		UserDetailsDTO result = userRepository.findUserDetailsById(userId)
				.orElseThrow(() -> new ApiException(UserErrorType.NOT_FOUND));
		
		return userMapper.localize(result);
	}

	public List<UserResponseDTO> findAll() {
		return userRepository.findAllUser();
	}

	@Transactional
	public UserDetailsDTO update(UserRequestDTO userDTO) {
		String userId = currentUserProvider.currentUserId();
		User user = findById(userId);
		
		validationService.validateTakenFields(userDTO);
		
		User updatedUser = userRepository.save(user.update(userDTO));
				
		return userMapper.toDetailsDto(updatedUser);
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
	}
	
	public UserEnums getEnums() {
		return userMapper.getLocalizedEnums();
	}
	
	@Cacheable(value = "tokenVersion", key = "#userId")
	public AuthMetadata getTokenVersion(String userId) {
	    AuthMetadata authMetadata = userRepository.findTokenVersionById(userId);
	    
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
