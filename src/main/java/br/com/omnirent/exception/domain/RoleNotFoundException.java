package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.DomainException;

public class RoleNotFoundException extends DomainException {
	private static final long serialVersionUID = 1L;

	public RoleNotFoundException(String role) {
		super("Role not found: " + role);
	}

}
