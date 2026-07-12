package br.com.omnirent.notification.context;

import java.math.BigDecimal;

import br.com.omnirent.payment.enums.PaymentProvider;

public record PaymentNotificationData(
	    String itemName,
	    BigDecimal amount,
	    String currency,
	    PaymentProvider provider,
	    String url,
	    UserNotificationData renterData,
	    UserNotificationData ownerData
	) {}
