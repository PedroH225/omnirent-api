package br.com.omnirent.exception.infrastructure;

import br.com.omnirent.exception.common.InfrastructureException;

public class EmailDeliveryException extends InfrastructureException {
	private static final long serialVersionUID = 1L;

	public EmailDeliveryException(Throwable cause) {
        super("Failed to deliver email", cause);
	}

}
