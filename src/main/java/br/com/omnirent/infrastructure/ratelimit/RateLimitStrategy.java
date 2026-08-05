package br.com.omnirent.infrastructure.ratelimit;

import org.springframework.http.HttpMethod;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RateLimitStrategy {

	LOGIN(HttpMethod.POST,"/api/auth/login", 5, 20),
	UPLOAD_IMAGE(HttpMethod.POST, "/api/item/{itemId}/images", 5, 10),
	CREATE(HttpMethod.POST, "/api/**", 20, 20),
	EDIT(HttpMethod.PUT, "/api/**", 20, 20),
	PATCH(HttpMethod.PATCH, "/api/**", 30, 20),
	DEFAULT(null,"/api/**", 50, 100);
	
	private HttpMethod method;
	
	private String uri;
		
	private int userMaxRequests;
	
	private int ipMaxRequests;
}
