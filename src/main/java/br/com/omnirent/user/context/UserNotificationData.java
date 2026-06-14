package br.com.omnirent.user.context;

public record UserNotificationData(
	    String userId,
	    String username,
	    String email,
	    String locale
	    ) {}
