package br.com.omnirent.payment.dto;

public record StripeCheckoutSession(
        String sessionId,
        String url
) {}