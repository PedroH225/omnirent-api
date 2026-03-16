package br.com.omnirent.user;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

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

	public List<UserResponseDTO> findAll() {
		return UserMapper.toDto(userRepository.findAll());
	}
}
