package br.com.omnirent.notification.context;

public record RentalNotificationData(
		String itemName,
		UserNotificationData renterData,
		UserNotificationData ownerData
		) {}
