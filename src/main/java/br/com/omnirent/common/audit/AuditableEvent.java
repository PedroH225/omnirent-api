package br.com.omnirent.common.audit;

import br.com.omnirent.common.event.DomainEvent;

public interface AuditableEvent extends DomainEvent {
	
	String actorId();
	
	Object oldData();
	
	Object newData();
}
