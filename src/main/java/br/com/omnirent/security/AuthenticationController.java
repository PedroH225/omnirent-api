package br.com.omnirent.security;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.security.dto.RegisterDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
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
