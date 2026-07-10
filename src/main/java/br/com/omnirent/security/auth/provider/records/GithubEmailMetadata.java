package br.com.omnirent.security.auth.provider.records;

public record GithubEmailMetadata(
        String email,
        boolean verified
) {}
