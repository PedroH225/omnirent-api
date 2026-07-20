package br.com.omnirent.common.enums;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;

@Getter
public enum ItemStatus {
	ANALISYS,
	AVAILABLE,
	RENTED,
	UNAVAILABLE,
	BLOCKED;
	
	private Set<ItemStatus> allowedTransitions = new HashSet<>();
	
	static {
		ANALISYS.allowedTransitions = Set.of(AVAILABLE);
		AVAILABLE.allowedTransitions = Set.of(RENTED, UNAVAILABLE, BLOCKED);
		UNAVAILABLE.allowedTransitions = Set.of(AVAILABLE, BLOCKED);
		RENTED.allowedTransitions = Set.of(UNAVAILABLE);
	}
	
	public Set<ItemStatus> getAllowedTransitions() {
		return allowedTransitions;
	}
	
	public boolean canTransition(ItemStatus target) {
		return allowedTransitions.contains(target);
	}

	public String getMessageKey() {
		return "item.status." + name();
	}
}
