package br.com.omnirent.payment.dto;

public sealed interface PaymentHistoryEntryResponse
	permits PaymentCreatedResponse, PaymentConfirmedResponse, PaymentStatusChangeResponse {}
