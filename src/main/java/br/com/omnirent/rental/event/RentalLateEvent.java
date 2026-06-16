package br.com.omnirent.rental.event;

import br.com.omnirent.infrastructure.IntegrationEvent;

public record RentalLateEvent(
		String rentalId
		) implements IntegrationEvent {}
