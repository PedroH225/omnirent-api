package br.com.omnirent.exception.infrastructure;

import br.com.omnirent.exception.common.InfrastructureException;

public class NotificationDataNotException extends InfrastructureException {

	private static final long serialVersionUID = 1L;

	public NotificationDataNotException() {
		super("Notification data not found.");
	}

}
