package br.com.omnirent.security.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.security.dto.RegisterDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
	
	private AuthenticationService authenticationService;
	
	@PostMapping("/register")
	public ResponseEntity<Object> register(
			@RequestBody @Valid RegisterDTO registerDTO,
			HttpServletRequest request) {
		return authenticationService.register(registerDTO, request);
	}
	
	@PostMapping("/login")
	public void login(
			@RequestBody LoginDTO loginDTO, 
			HttpServletRequest request, HttpServletResponse response) {
		authenticationService.login(loginDTO, request, response);
	}
	
	@PatchMapping("/logout")
	public void logout(HttpServletResponse response) {
		authenticationService.logout(response);
	}

}
