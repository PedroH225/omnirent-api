package br.com.omnirent.payment.dto;

import java.math.BigDecimal;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.payment.enums.PaymentProvider;

public record PaymentCreatedResponse(
	    PaymentStatus status,
	    PaymentProvider provider,
	    BigDecimal amount,
	    String currency
		) implements PaymentHistoryEntryResponse {}
