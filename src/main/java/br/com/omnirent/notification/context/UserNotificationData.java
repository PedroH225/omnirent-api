package br.com.omnirent.notification.context;

import br.com.omnirent.common.enums.UserStatus;

public record UserNotificationData(
	    String userId,
	    String username,
	    String email,
	    UserStatus status,
	    String locale
	    ) {}
