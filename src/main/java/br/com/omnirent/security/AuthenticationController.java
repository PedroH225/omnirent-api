package br.com.omnirent.security;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController("/auth")
public class AuthenticationController {
	
	private AuthenticationService authenticationService;
	
	@PostMapping("/register")
	public ResponseEntity<Object> register(@RequestBody RegisterDTO registerDTO) {
		return authenticationService.register(registerDTO);
	}
	
	@PostMapping("/login")
	public Map<String, String> login(@RequestBody LoginDTO loginDTO) {
		return authenticationService.login(loginDTO);
	}

}
