package br.com.omnirent.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.omnirent.security.domain.AuthenticatedUser;

public final class SecurityTestUtils {

    public static void setAuthenticatedUser(String userId) {    	
        AuthenticatedUser principal =
            new AuthenticatedUser(userId, Collections.emptyList(), 1, 1);

        Authentication auth =
            new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
            );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
