package br.com.omnirent.security.auth;

import br.com.omnirent.security.auth.provider.AuthProvider;

public record ProviderUserMetadata(
		AuthProvider provider,
        String sub,
        String email,
        Boolean emailVerified,
        String name,
        String picture,
        String locale
		) {}
