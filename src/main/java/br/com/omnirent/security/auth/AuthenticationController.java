package br.com.omnirent.security.auth;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import br.com.omnirent.security.dto.LoginDTO;
import br.com.omnirent.security.dto.RegisterDTO;
import jakarta.servlet.http.HttpServletRequest;
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
	public Map<String, String> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request) {
		return authenticationService.login(loginDTO, request);
	}

}
