package br.com.omnirent.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.security.AuthenticationService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

	private UserService userService;
	
	private AuthenticationService authenticationService;
	
	@GetMapping("/find/{id}")
	public User findById(@PathVariable String id) {
		return userService.findById(id);
	}
	
	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody RegisterDTO registerDTO) {
		return authenticationService.register(registerDTO);
	}
	
}
