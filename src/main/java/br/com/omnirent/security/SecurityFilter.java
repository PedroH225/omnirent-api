package br.com.omnirent.security;

import java.io.IOException;
import java.net.SecureCacheResponse;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.omnirent.config.GlobalConfigHolder;
import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.user.AuthMetadata;
import br.com.omnirent.user.UserRepository;
import br.com.omnirent.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter{
    @Autowired 
    TokenService tokenService;
    
    @Autowired
	private UserService userService;
    
    @Autowired
    private GlobalConfigHolder globalConfigHolder;
 
    @Override
    protected void doFilterInternal
    (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    		throws ServletException, IOException {
        String token = this.recoverToken(request);

        if (token != null) {
            try {            			
                var decoded = tokenService.validateToken(token);
                
            	var id = decoded.getSubject();
                
                var rolesClaim = decoded.getClaim("roles").asList(String.class);

                List<SimpleGrantedAuthority> authorities =
                    rolesClaim == null ? List.of() :
                    rolesClaim.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();
                
                Integer tokenVer = decoded.getClaim("ver").asInt();
                AuthMetadata authMetadata = userService.getTokenVersion(id);
                if (!tokenVer.equals(authMetadata.getTokenVersion())) {
					throw new Exception();
				} 
                
                Integer globalVer = globalConfigHolder.getGlobalTokenVersion();
                Integer currGlobalVer = decoded.getClaim("ver").asInt();
                if (!globalVer.equals(currGlobalVer)) {
					throw new Exception();
				}
                
                AuthenticatedUser authenticatedUser = new AuthenticatedUser
                		(id, authorities, tokenVer, globalVer);

                var auth = new UsernamePasswordAuthenticationToken(
                    authenticatedUser, null, authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
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
