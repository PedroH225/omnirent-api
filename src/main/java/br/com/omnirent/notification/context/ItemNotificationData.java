package br.com.omnirent.notification.context;

public record ItemNotificationData(
		String name,
		UserNotificationData ownerData
		) {}
