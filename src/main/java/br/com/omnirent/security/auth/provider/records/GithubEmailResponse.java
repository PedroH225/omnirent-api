package br.com.omnirent.security.auth.provider.records;

public record GithubEmailResponse(
        String email,
        boolean primary,
        boolean verified
) {}
