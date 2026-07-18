package br.com.omnirent.infrastructure.ratelimit;

import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RateLimitStrategy {

	LOGIN(HttpMethod.POST,"/api/auth/login", 5, 20),
	UPLOAD(HttpMethod.POST, "/api/item/{itemId}/images", 5, 10),
	DEFAULT(null,"/api/**", 20, 30);
	
	private HttpMethod method;
	
	private String uri;
		
	private int userMaxRequests;
	
	private int ipMaxRequests;
}
