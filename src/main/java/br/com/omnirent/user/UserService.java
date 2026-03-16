package br.com.omnirent.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.omnirent.security.SecurityUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {

	private UserRepository userRepository;
	
	public User findById(String id) {
		Optional<User> user = userRepository.findById(id);
		
		if (user.isEmpty()) {
			throw new RuntimeException("User not found.");
		}
		
		return user.get();
	}

	public UserDetailsDTO getUserDetailsById(String id) {
		return UserMapper.toDetailsDto(findById(id));
	}

	public List<UserResponseDTO> findAll() {
		return UserMapper.toDto(userRepository.findAll());
	}

	public UserDetailsDTO update(UserRequestDTO userDTO) {
		User user = findById(SecurityUtils.currentUserId());
		
		User updatedUser = userRepository.save(user.update(userDTO));
		
		return UserMapper.toDetailsDto(updatedUser);
	}

	public void deactivateUser(String userId) {
		User user = findById(userId);
		
		userRepository.save(user.deactivate());
		
	}

	public void activateUser(String userId) {
		User user = findById(userId);
		
		userRepository.save(user.activate());		
	}
}
