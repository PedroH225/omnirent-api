package br.com.omnirent.notification.context;

import java.time.LocalDateTime;

public record RentalLateNotificationData(
		String itemName,
		UserNotificationData renterData,
		UserNotificationData ownerData,
		LocalDateTime endDate
		) {}
