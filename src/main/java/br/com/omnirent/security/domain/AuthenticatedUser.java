package br.com.omnirent.security.domain;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticatedUser {

	private String id;
	
	private List<SimpleGrantedAuthority> authorities;
	
	private Integer tokenVersion;
	
	private Integer globalVersion;
}
