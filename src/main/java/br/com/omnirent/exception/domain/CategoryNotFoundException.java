package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public CategoryNotFoundException() {
		super("Category not found.");
		
	}

}
