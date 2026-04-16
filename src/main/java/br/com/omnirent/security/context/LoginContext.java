package br.com.omnirent.security.context;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LoginContext {

	private String id;
	
	private String email;
	
	private String password;
	
	private Integer tokenVersion;
	
	private Integer globalVersion;
}
