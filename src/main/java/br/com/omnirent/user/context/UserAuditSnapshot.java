package br.com.omnirent.user.context;

import br.com.omnirent.common.audit.AuditBody;

public record UserAuditSnapshot(
        String id,
        String name,
        String username,
        String email,
        String birthDate
		) implements AuditBody {}