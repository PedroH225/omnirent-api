package br.com.omnirent.infrastructure.ratelimit;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.omnirent.exception.common.ApiErrorResponseWriter;
import br.com.omnirent.exception.common.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

	private final ClientIdentifierResolver identifierResolver;
	
	private final RateLimitService rateLimitService;
	
	private final ApiErrorResponseWriter apiErrorWriter;
	
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain) throws ServletException, IOException {

		List<ClientIdentifier> clientIdentifiers = identifierResolver.resolveIdentifier(request);
		
		try {
			for (ClientIdentifier clientIdentifier : clientIdentifiers) {
				rateLimitService.verifyRequest(clientIdentifier);
			}
		} catch (ApiException e) {
			apiErrorWriter.onApiError(request, response, e);
			return;
		}
		filterChain.doFilter(request, response);
	}
}
