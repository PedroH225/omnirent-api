package br.com.omnirent.user;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import br.com.omnirent.exception.domain.UserNotFoundException;
import br.com.omnirent.security.SecurityUtils;
import br.com.omnirent.user.domain.AuthMetadata;
import br.com.omnirent.user.domain.User;
import br.com.omnirent.user.domain.UserDetailsDTO;
import br.com.omnirent.user.domain.UserRequestDTO;
import br.com.omnirent.user.domain.UserResponseDTO;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {
	
	private UserMapper userMapper;

	private UserRepository userRepository;
		
	public User findById(String id) {
		Optional<User> user = userRepository.findById(id);
		
		if (user.isEmpty()) {
			throw new UserNotFoundException();
		}
		
		return user.get();
	}
	
	public UserDetailsDTO getUserDetailsById(String id) {
		Optional<UserDetailsDTO> user = userRepository.findUserDetailsById(id);

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
		User user = findById(SecurityUtils.currentUserId());
				
		User updatedUser = userRepository.save(user.update(userDTO));
				
		return userMapper.toDetailsDto(updatedUser);
	}

	@Transactional
	public void deactivateUser(String userId) {
		User user = findById(userId);
		
		userRepository.save(user.deactivate());
	}

	@Transactional
	public void activateUser(String userId) {
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
