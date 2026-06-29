package br.com.omnirent.payment.event;

import java.math.BigDecimal;

import br.com.omnirent.infrastructure.IntegrationEvent;

public record PaymentRequestedEvent(
        String rentalId,
        String userId,
        BigDecimal amount,
        String currency
) implements IntegrationEvent {}
