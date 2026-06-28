package br.com.omnirent.notification.context;

import java.time.Instant;

public record RentalLateNotificationData(
		String itemName,
		UserNotificationData renterData,
		UserNotificationData ownerData,
		Instant endDate
		) {}
