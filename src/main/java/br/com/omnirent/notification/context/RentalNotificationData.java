package br.com.omnirent.notification.context;

public record RentalNotificationData(
		String itemName,
		
		String ownerUsername,
		String ownerEmail,
		String ownerLocale,
		
		String renterUsername,
		String renterEmail,
		String renterLocale
		) {}
