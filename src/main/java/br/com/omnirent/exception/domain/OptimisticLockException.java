package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.ConflictException;

public class OptimisticLockException extends ConflictException {
	private static final long serialVersionUID = 1L;

	public OptimisticLockException() {
		super("Item was modified before update.");
	}

}
