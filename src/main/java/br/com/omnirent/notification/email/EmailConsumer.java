package br.com.omnirent.notification.email;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.infrastructure.NotificationDataNotException;
import br.com.omnirent.item.event.ItemCreatedEvent;
import br.com.omnirent.notification.JpaNotificationQueryRepository;
import br.com.omnirent.notification.context.RentalInUseNotificationData;
import br.com.omnirent.notification.context.RentalLateNotificationData;
import br.com.omnirent.notification.context.RentalNotificationData;
import br.com.omnirent.notification.context.UserNotificationData;
import br.com.omnirent.rental.event.RentalCanceledEvent;
import br.com.omnirent.rental.event.RentalCreatedEvent;
import br.com.omnirent.rental.event.RentalExpiredEvent;
import br.com.omnirent.rental.event.RentalInUseEvent;
import br.com.omnirent.rental.event.RentalLateEvent;
import br.com.omnirent.rental.event.RentalStatusChangedEvent;
import br.com.omnirent.security.event.UserRegisteredEvent;
import br.com.omnirent.user.event.UserStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(
	    value = "app.mail.consumers.enabled",
	    havingValue = "true",
	    matchIfMissing = true
	)
@Component
@RequiredArgsConstructor
@RabbitListener(queues = "email.queue", containerFactory = "rabbitListenerContainerFactory")
@Slf4j
public class EmailConsumer {

    private final EmailService emailService;
    
    private final RentalEmailService rentalEmailService;
    
    private final JpaNotificationQueryRepository queryRepository;
    
    @RabbitHandler
    public void handle(UserRegisteredEvent event) {
        emailService.sendWelcomeEmail(event);
    }
    
    @RabbitHandler
    public void handle(UserStatusChangeEvent event) {
    	UserNotificationData data = queryRepository.findNotificationData(event.userId())
    			.orElseThrow(() -> new NotificationDataNotException());
;
        emailService.sendUserStatusChanged(data);
    }
    
    @RabbitHandler
    public void handle(ItemCreatedEvent event) {
        emailService.sendNewItemEmail(event);
    }
    
    @RabbitHandler
    public void handle(RentalCreatedEvent event) {
    	boolean truee = true;
    	if (truee) {
			throw new IllegalArgumentException("Illegal argument!!");
		}
    	
    	RentalNotificationData notificationData =
    			queryRepository.findRentalNotificationData(event.entityId())
    			.orElseThrow(() -> new NotificationDataNotException());
    	
    	rentalEmailService.sendRentalCreatedToOwner(notificationData);
    	rentalEmailService.sendRentalCreatedToRenter(notificationData);
    }
    
    @RabbitHandler
    public void handle(RentalInUseEvent event) {
    	RentalInUseNotificationData notificationData =
    			queryRepository.findRentalInUseNotificationData(event.entityId())
    			.orElseThrow(() -> new NotificationDataNotException());

    	rentalEmailService.sendRentalInUseToOwner(notificationData);
    	rentalEmailService.sendRentalInUseToRenter(notificationData);
    }
    
    @RabbitHandler
    public void handle(RentalLateEvent event) {
    	RentalLateNotificationData notificationData =
    			queryRepository.findRentalLateNotificationData(event.rentalId())
    			.orElseThrow(() -> new NotificationDataNotException());
    	
    	rentalEmailService.sendRentalLateToOwner(notificationData);
    	rentalEmailService.sendRentalLateToRenter(notificationData);
    }
    
    @RabbitHandler
    public void handle(RentalCanceledEvent event) {
    	RentalNotificationData notificationData =
    			queryRepository.findRentalNotificationData(event.entityId())
    			.orElseThrow(() -> new NotificationDataNotException());
    	
    	rentalEmailService.sendRentalCanceledToOwner(notificationData);
    	rentalEmailService.sendRentalCanceledToRenter(notificationData);
    }
    
    @RabbitHandler
    public void handle(RentalExpiredEvent event) {
    	RentalNotificationData notificationData =
    			queryRepository.findRentalNotificationData(event.entityId())
    			.orElseThrow(() -> new NotificationDataNotException());
    	
    	rentalEmailService.sendRentalExpiredToOwner(notificationData);
    	rentalEmailService.sendRentalExpiredToRenter(notificationData);
    }
    
    @RabbitHandler
    public void handle(RentalStatusChangedEvent event) {
    	RentalStatus newStatus = event.currentBody().status();
    	RentalNotificationData notificationData =
    			queryRepository.findRentalNotificationData(event.entityId())
    			.orElseThrow(() -> new NotificationDataNotException());
    	
    	if (newStatus == RentalStatus.CONFIRMED) {
        	rentalEmailService.sendRentalConfirmedToOwner(notificationData);
        	rentalEmailService.sendRentalConfirmedToRenter(notificationData);
		}
    	else if (newStatus == RentalStatus.PREPARING) {
			rentalEmailService.sendRentalPreparingToRenter(notificationData);
		}
    	else if (newStatus == RentalStatus.SHIPPED) {
    		rentalEmailService.sendRentalShippedToRenter(notificationData);
    	}
    	else if (newStatus == RentalStatus.RETURN_REQUESTED) {
			rentalEmailService.sendRentalReturnRequestedToOwner(notificationData);
			rentalEmailService.sendRentalReturnRequestedToRenter(notificationData);
		}
    	else if (newStatus == RentalStatus.RETURN_SHIPPED) {
    		rentalEmailService.sendRentalReturnShippedToOwner(notificationData);
			rentalEmailService.sendRentalReturnShippedToRenter(notificationData);
    	}
    	else if(newStatus == RentalStatus.RETURNED) {
    		rentalEmailService.sendRentalReturnedToOwner(notificationData);
			rentalEmailService.sendRentalReturnedToRenter(notificationData);
    	}
    }
}
