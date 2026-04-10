package br.com.omnirent.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.omnirent.user.User;

public class SecurityUtils {

    public static String currentUserId() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        return authenticatedUser.getId();
    }
}
