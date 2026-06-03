package br.com.omnirent.user.context;

public record UserAuditSnapshot(
        String id,
        String name,
        String username,
        String email,
        String birthDate
) {
}