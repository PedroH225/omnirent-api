package br.com.omnirent.payment.dto;

import java.time.Instant;

import br.com.omnirent.common.enums.PaymentStatus;

public record PaymentConfirmedResponse(
	    PaymentStatus status,
	    Instant paidAt
	) implements PaymentHistoryEntryResponse {}	
