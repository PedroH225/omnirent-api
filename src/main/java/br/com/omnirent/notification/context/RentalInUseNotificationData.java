package br.com.omnirent.notification.context;

import java.time.Instant;

public record RentalInUseNotificationData(
		String itemName,
		UserNotificationData renterData,
		UserNotificationData ownerData,
		Instant startDate,
		Instant endDate
		) {}
