package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.DomainException;

public class OptimisticLockException extends DomainException {
	private static final long serialVersionUID = 1L;

	public OptimisticLockException(String context, String id) {
		super(String.format("%s was modified before update, id: %s",
				context, id));
	}

}
