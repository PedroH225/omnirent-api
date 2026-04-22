package br.com.omnirent.user;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import br.com.omnirent.exception.domain.UserNotFoundException;
import br.com.omnirent.security.CurrentUserProvider;
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
	
	private CurrentUserProvider currentUserProvider;
	
	public void requireExistence(String userId) {
		if (!userRepository.verifyUser(userId)) {
			throw new UserNotFoundException();
		}
	}
	
	public User getUserReference(String userId) {
		User user = userRepository.getReferenceById(userId);
		
		return user;
	}
		
	public User findById(String id) {
		Optional<User> user = userRepository.findById(id);
		
		if (user.isEmpty()) {
			throw new UserNotFoundException();
		}
		
		return user.get();
	}
	
	public UserDetailsDTO getUserDetailsById() {
		String userId = currentUserProvider.currentUserId();
		Optional<UserDetailsDTO> user = userRepository.findUserDetailsById(userId);

		if (user.isEmpty()) {
			throw new UserNotFoundException();
		}
		
		return user.get();
	}

	public List<UserResponseDTO> findAll() {
		return userRepository.findAllUser();
	}

	@Transactional
	public UserDetailsDTO update(UserRequestDTO userDTO) {
		String userId = currentUserProvider.currentUserId();
		User user = findById(userId);
				
		User updatedUser = userRepository.save(user.update(userDTO));
				
		return userMapper.toDetailsDto(updatedUser);
	}

	@Transactional
	public void deactivateUser() {
		String userId = currentUserProvider.currentUserId();
		User user = findById(userId);
		
		userRepository.save(user.deactivate());
	}

	@Transactional
	public void activateUser() {
		String userId = currentUserProvider.currentUserId();
		User user = findById(userId);
		
		userRepository.save(user.activate());		
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
