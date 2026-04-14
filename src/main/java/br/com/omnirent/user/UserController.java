package br.com.omnirent.user;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.security.SecurityUtils;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.user.dto.UserResponseDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

	private UserService userService;
	
	@GetMapping("/findAll")
	public List<UserResponseDTO> findAll() {
		return userService.findAll();
	}
	
	@GetMapping("/find")
	public UserDetailsDTO findById() {
		return userService.getUserDetailsById(SecurityUtils.currentUserId());
	}
	
	@PutMapping("/update")
	public UserDetailsDTO updateUser(@RequestBody UserRequestDTO user) {
		return userService.update(user);
	}
	
	@PatchMapping("/deactivate")
	public void deactivateUser() {
		userService.deactivateUser(SecurityUtils.currentUserId());

	}
	
	@PatchMapping("/activate")
	public void activateUser() {
		userService.activateUser(SecurityUtils.currentUserId());

	}
}
