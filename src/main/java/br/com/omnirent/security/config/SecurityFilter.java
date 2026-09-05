package br.com.omnirent.security.config;

import java.io.IOException;

import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.omnirent.exception.common.ApiException;
import br.com.omnirent.security.CookieService;
import br.com.omnirent.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter{
	
    private TokenService tokenService;
    
    private CookieService cookieService;
    
    private CustomAuthenticationEntryPoint authenticationEntryPoint;
 
    @Override
    protected void doFilterInternal
    (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    		throws ServletException, IOException, ApiException {
        String token = this.recoverToken(request);

        if (token != null) {
            try {            			
                var decoded = tokenService.validateToken(token);
                
                SecurityContextHolder.getContext()
                .setAuthentication(tokenService.authenticate(decoded));

            } catch (CredentialsExpiredException ex) {
                SecurityContextHolder.clearContext();
                cookieService.removeAccessTokenCookie(response);
                authenticationEntryPoint.commence(request, response, ex);
                return;
            }
            catch (AuthenticationException ex) {
                SecurityContextHolder.clearContext();
                authenticationEntryPoint.commence(request, response, ex);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {

        return cookieService.getAccessToken(request)
                .orElse(null);
    }
}
