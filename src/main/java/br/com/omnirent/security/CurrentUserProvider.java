package br.com.omnirent.security;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    
    public List<SimpleGrantedAuthority> getAuthorities() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        AuthenticatedUser authenticatedUser =
                (AuthenticatedUser) authentication.getPrincipal();

        return authenticatedUser.getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .toList();
    }
}
