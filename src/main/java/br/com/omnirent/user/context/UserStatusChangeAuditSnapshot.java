package br.com.omnirent.user.context;

import br.com.omnirent.common.audit.AuditBody;
import br.com.omnirent.common.enums.UserStatus;

public record UserStatusChangeAuditSnapshot(
		UserStatus status
		) implements AuditBody {}
