package br.com.omnirent.notification.context;

public record UserNotificationData(
	    String userId,
	    String username,
	    String email,
	    String locale
	    ) {}
