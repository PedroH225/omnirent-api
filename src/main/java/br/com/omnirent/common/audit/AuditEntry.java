package br.com.omnirent.common.audit;

import java.time.Instant;

public record AuditEntry(
	    AuditAction action,
	    String entityId,
	    String actorId,
	    AuditBody currentBody,
	    AuditBody previousBody,
	    Instant occurredAt
	) {}
