package br.com.omnirent.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.omnirent.exception.common.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class SecurityFilter extends OncePerRequestFilter{
	
    private TokenService tokenService;
    
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

            } catch (AuthenticationException ex) {
                SecurityContextHolder.clearContext();

                authenticationEntryPoint.commence(request, response, ex);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request){
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
