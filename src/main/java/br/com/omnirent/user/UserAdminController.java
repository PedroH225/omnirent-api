package br.com.omnirent.user;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.common.enums.UserStatus;
import br.com.omnirent.common.page.PageResponseDTO;
import br.com.omnirent.user.context.UserFilter;
import br.com.omnirent.user.dto.UserSummaryDTO;
import lombok.RequiredArgsConstructor;

@RequestMapping("/admin/users")
@RestController
@RequiredArgsConstructor
public class UserAdminController {

	private final UserService userService;

	@GetMapping
	public PageResponseDTO<UserSummaryDTO> findUsers(
			@RequestParam(required = false) String username, 
			@RequestParam(required = false) UserStatus status,
			Pageable pageable) {
		UserFilter filters = new UserFilter(username, status);
		
		return userService.searchUsers(filters, pageable);
	}
	
	@PatchMapping("/status/{id}")
	public void toggleUserBanStatus(@PathVariable String id) {
		userService.toggleUserBanStatus(id);
	}
}
