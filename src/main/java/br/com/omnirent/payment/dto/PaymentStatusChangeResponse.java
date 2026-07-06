package br.com.omnirent.payment.dto;

import br.com.omnirent.common.enums.PaymentStatus;

public record PaymentStatusChangeResponse(
		PaymentStatus status
	) implements PaymentHistoryEntryResponse {}
