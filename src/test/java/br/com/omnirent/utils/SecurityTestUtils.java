package br.com.omnirent.utils;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import br.com.omnirent.security.domain.AuthenticatedUser;
import br.com.omnirent.user.domain.User;

public final class SecurityTestUtils {

    public static void setAuthenticatedUser(User user) {    	
        AuthenticatedUser principal =
            new AuthenticatedUser(user.getId(), user.getEmail(), user.getPassword(),  Collections.emptyList(), 1, 1);

        Authentication auth =
            new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
            );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    public static RequestPostProcessor auth(User user) {
        AuthenticatedUser principal =
                new AuthenticatedUser(user.getId(), user.getEmail(), user.getPassword(),  Collections.emptyList(), 1, 1);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        return authentication(auth);
    }
    
    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
