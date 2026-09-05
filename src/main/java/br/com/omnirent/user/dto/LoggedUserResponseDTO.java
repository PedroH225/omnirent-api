package br.com.omnirent.user.dto;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import lombok.Getter;

@Getter
public class LoggedUserResponseDTO {

	private String id;
	
	private String username;
	
	private String name;
	
	private String locale;
	
	private String timezone;
	
	private List<String> authorities;

	public LoggedUserResponseDTO(String id, String username, String name, String locale, String timezone) {
		this.id = id;
		this.username = username;
		this.name = name;
		this.locale = locale;
		this.timezone = timezone;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities.stream()
		        .map(GrantedAuthority::getAuthority)
		        .toList();
	}
	
}