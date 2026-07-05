package br.com.omnirent.payment.event;

import br.com.omnirent.infrastructure.IntegrationEvent;

public record PaymentExpirationRequestEvent(
		String paymentId) implements IntegrationEvent {}
