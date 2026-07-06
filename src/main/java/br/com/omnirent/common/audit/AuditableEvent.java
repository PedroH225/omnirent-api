package br.com.omnirent.common.audit;

import java.time.Instant;

public interface AuditableEvent<T extends AuditBody> {

	AuditAction action();

	String actorId();
	
	String entityId();
	
	Instant occurredAt();

	T currentBody();
	
	T previousBody();
	
}
