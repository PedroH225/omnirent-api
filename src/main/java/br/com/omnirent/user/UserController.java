package br.com.omnirent.user;


import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.common.enums.UserEnums;
import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.page.PageResponseDTO;
import br.com.omnirent.user.context.UserFilter;
import br.com.omnirent.user.dto.LoggedUserResponseDTO;
import br.com.omnirent.user.dto.UserDetailsDTO;
import br.com.omnirent.user.dto.UserRequestDTO;
import br.com.omnirent.user.dto.UserSummaryDTO;
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
	
	@GetMapping("/admin/find")
	public PageResponseDTO<UserSummaryDTO> findUsers(
			@RequestParam(required = false) String username, 
			@RequestParam(required = false) UserStatus status,
			Pageable pageable) {
		UserFilter filters = new UserFilter(username, status);
		
		return userService.searchUsers(filters, pageable);
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
	
	@PatchMapping("/admin/status/{id}")
	public void toggleUserBanStatus(@PathVariable String id) {
		userService.toggleUserBanStatus(id);
	}
}
