package br.com.omnirent.infrastructure.ratelimit;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.com.omnirent.security.domain.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class ClientIdentifierResolver {

	public List<ClientIdentifier> resolveIdentifier(HttpServletRequest request) {		
		List<ClientIdentifier> identities = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext()
		        .getAuthentication();

		Object principal = authentication != null
		        ? authentication.getPrincipal()
		        : null;
		
		if (principal instanceof AuthenticatedUser user) {
			identities.add(new ClientIdentifier(user.getId(), ClientIdentifierType.USER));
		}
		identities.add(new ClientIdentifier(extractIp(request), ClientIdentifierType.IP));
		
		return identities;
	}
	
	private String extractIp(HttpServletRequest request) {
		String forwarded = request.getHeader("X-Forwarded-For");

		if (forwarded != null && !forwarded.isBlank()) {
			return forwarded.split(",")[0].trim();
		}

		return request.getRemoteAddr();
	}
}
