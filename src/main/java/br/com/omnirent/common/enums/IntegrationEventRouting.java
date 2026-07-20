package br.com.omnirent.common.enums;

import br.com.omnirent.infrastructure.IntegrationEvent;
import br.com.omnirent.item.event.ItemApprovedEvent;
import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.item.event.ItemRejectedEvent;
import br.com.omnirent.payment.event.PaymentConfirmedEvent;
import br.com.omnirent.payment.event.PaymentCreatedEvent;
import br.com.omnirent.payment.event.PaymentExpirationRequestEvent;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.payment.event.PaymentStatusChangedEvent;
import br.com.omnirent.rental.event.RentalCanceledEvent;
import br.com.omnirent.rental.event.RentalCreatedEvent;
import br.com.omnirent.rental.event.RentalExpiredEvent;
import br.com.omnirent.rental.event.RentalInUseEvent;
import br.com.omnirent.rental.event.RentalLateEvent;
import br.com.omnirent.rental.event.RentalStatusChangedEvent;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.event.UserStatusChangeEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IntegrationEventRouting {
	
	PAYMENT_REQUESTED(PaymentRequestedEvent.class, "payment.requested"),
	PAYMENT_EXPIRATION(PaymentExpirationRequestEvent.class, "payment.expired"),
	PAYMENT_CREATED(PaymentCreatedEvent.class, "payment.created"),
	PAYMENT_CONFIRMED(PaymentConfirmedEvent.class, "payment.confirmed"),
	PAYMENT_STATUS_CHANGED(PaymentStatusChangedEvent.class, "payment.status.changed"),
	
	NEW_ITEM(ItemCreatedEvent.class, "item.created"),
	ITEM_REJECTED(ItemRejectedEvent.class, "item.rejected"),
	ITEM_APROVED(ItemApprovedEvent.class, "item.aproved"),
	
	RENTAL_CREATED(RentalCreatedEvent.class, "rental.created"),
	RENTAL_STATUS_CHANGED(RentalStatusChangedEvent.class, "rental.status.changed"),
	RENTAL_IN_USE(RentalInUseEvent.class, "rental.status.in_use"),
	RENTAL_LATE(RentalLateEvent.class, "rental.status.late"),
	RENTAL_CANCELED(RentalCanceledEvent.class, "rental.status.canceled"),
	RENTAL_EXPIRED(RentalExpiredEvent.class, "rental.status.expired"),
	
	USER_REGISTERED(UserRegisteredEvent.class, "user.registered"),
	USER_STATUS_CHANGED(UserStatusChangeEvent.class,  "user.status_changed");

	private Class<? extends IntegrationEvent> eventClass;
	
	private String key;
	
	public static IntegrationEventRouting from(IntegrationEvent event) {
	    for (IntegrationEventRouting routing : values()) {
	        if (routing.eventClass.equals(event.getClass())) {
	            return routing;
	        }
	    }

	    throw new IllegalArgumentException(
	            "No routing configured for event: " + event.getClass().getSimpleName()
	    );
	}
}
