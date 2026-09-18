package br.com.omnirent.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.common.enums.UserEnums;
import br.com.omnirent.user.dto.LoggedUserResponseDTO;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserPreferencesDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

	private UserService userService;
	
	@GetMapping("/find")
	public UserDetailsDTO findById() {
		return userService.getUserDetailsById();
	}
	
	@GetMapping("/me")
	public LoggedUserResponseDTO findLoggedUser() {
		return userService.getLoggedUserData();
	}
	
	@GetMapping("/enums")
	public UserEnums getEnums() {
		return userService.getEnums();
	}
	
	@PutMapping("/update")
	public UserDetailsDTO updateUser(@RequestBody @Valid UserRequestDTO user) {
		return userService.update(user);
	}
	
	@PatchMapping("/changeStatus")
	public void changeUserStatus() {
		userService.changeUserStatus();
	}
	
	@PatchMapping("/changePreferences")
	public UserPreferencesDTO changeUserPreferences(@RequestBody UserPreferencesDTO preferences) {
		return userService.changePreferences(preferences);
	}
}
