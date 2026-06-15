package br.com.omnirent.notification.context;

import java.time.LocalDateTime;

public record RentalInUseNotificationData(
		String itemName,
		UserNotificationData renterData,
		UserNotificationData ownerData,
		LocalDateTime startDate,
		LocalDateTime endDate
		) {}
