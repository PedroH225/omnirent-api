package br.com.omnirent.exception.domain;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;


import br.com.omnirent.exception.common.BusinessException;

public class IllegalEnumerationException extends BusinessException {
	private static final long serialVersionUID = 1L;

	public <T extends Enum<T>> IllegalEnumerationException(Class<?> enumeration, String value) {
		super("Invalid " +
				StringUtils.join(
				StringUtils.splitByCharacterTypeCamelCase(
						enumeration.getSimpleName()), StringUtils.SPACE).toLowerCase() +
				": " + value, 
				HttpStatus.BAD_REQUEST, "Illegal enumeration");
	}

}
