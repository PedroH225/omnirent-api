package br.com.omnirent.common.audit;

import br.com.omnirent.address.context.AddressAuditSnapshot;
import br.com.omnirent.item.context.ItemAuditSnapshot;
import br.com.omnirent.item.context.ItemReassignedAuditSnapshot;
import br.com.omnirent.item.context.ItemStatusChangedAuditSnapshot;
import br.com.omnirent.payment.context.audit.PaymentConfirmedAuditSnapshot;
import br.com.omnirent.payment.context.audit.PaymentCreatedAuditSnapshot;
import br.com.omnirent.payment.context.audit.PaymentStatusChangedAuditSnapshot;
import br.com.omnirent.payment.event.PaymentStatusChangedEvent;
import br.com.omnirent.rental.context.RentalAuditSnapshot;
import br.com.omnirent.rental.context.RentalInUseAuditSnapshot;
import br.com.omnirent.rental.context.RentalStatusChangedAuditSnapshot;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.context.UserAuditSnapshot;
import br.com.omnirent.user.context.UserStatusChangeAuditSnapshot;
import lombok.Getter;

@Getter
public enum AuditAction {
	PAYMENT_STATUS_CHANGED(PaymentStatusChangedAuditSnapshot.class),
	PAYMENT_CREATED(PaymentCreatedAuditSnapshot.class),
	PAYMENT_CONFIRMED(PaymentConfirmedAuditSnapshot.class),
	RENTAL_CREATED(RentalAuditSnapshot.class),
	RENTAL_STATUS_CHANGED(RentalStatusChangedAuditSnapshot.class),
	RENTAL_IN_USE(RentalInUseAuditSnapshot.class),
	RENTAL_CANCELED(RentalStatusChangedAuditSnapshot.class),
	RENTAL_EXPIRED(RentalStatusChangedAuditSnapshot.class),
	ITEM_CREATED(ItemAuditSnapshot.class),
	ITEM_UPDATED(ItemAuditSnapshot.class),
	ITEM_STATUS_UPDATED(ItemStatusChangedAuditSnapshot.class),
	ITEM_CATEGORY_CHANGED(ItemReassignedAuditSnapshot.class),
	ITEM_ADDRESS_CHANGED(ItemReassignedAuditSnapshot.class),
	ADDRESS_CREATED(AddressAuditSnapshot.class),
	ADDRESS_UPDATED(AddressAuditSnapshot.class),
	ADDRESS_DELETED(AddressAuditSnapshot.class),
	USER_REGISTERED(UserAuditSnapshot.class),
	USER_UPDATED(UserAuditSnapshot.class),
	USER_STATUS_CHANGED(UserStatusChangeAuditSnapshot.class);

	private Class<? extends AuditBody> bodyClass;
	
    AuditAction(Class<? extends AuditBody> bodyClass) {
        this.bodyClass = bodyClass;
    }
}
