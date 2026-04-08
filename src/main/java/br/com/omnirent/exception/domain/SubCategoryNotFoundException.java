package br.com.omnirent.exception.domain;

import br.com.omnirent.exception.common.NotFoundException;

public class SubCategoryNotFoundException extends NotFoundException {
	private static final long serialVersionUID = 1L;

	public SubCategoryNotFoundException() {
		super("Subcategory not found.");
		
	}

}
