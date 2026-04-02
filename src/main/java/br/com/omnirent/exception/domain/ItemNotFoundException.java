package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public ItemNotFoundException() {
		super("Item not found.");
		
	}

}
