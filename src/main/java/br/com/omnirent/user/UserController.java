package br.com.omnirent.user;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.security.SecurityUtils;
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
}
