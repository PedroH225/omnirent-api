package br.com.omnirent.payment.dto;

public record CheckoutCompletedDTO(
		String rentalId,
		String checkoutUrl,
		String status
		) {}
