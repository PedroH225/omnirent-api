package br.com.omnirent.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.com.omnirent.security.domain.AuthenticatedUser;

@Component
public class CurrentUserProvider {

    public String currentUserId() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();

        return authenticatedUser.getId();
    }
}
