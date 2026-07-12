package br.com.omnirent.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.exception.infrastructure.NotificationDataNotException;
import br.com.omnirent.notification.JpaNotificationQueryRepository;
import br.com.omnirent.notification.context.RentalInUseNotificationData;
import br.com.omnirent.notification.context.RentalLateNotificationData;
import br.com.omnirent.notification.context.RentalNotificationData;
import br.com.omnirent.notification.email.service.RentalEmailService;
import br.com.omnirent.rental.event.RentalCanceledEvent;
import br.com.omnirent.rental.event.RentalCreatedEvent;
import br.com.omnirent.rental.event.RentalExpiredEvent;
import br.com.omnirent.rental.event.RentalInUseEvent;
import br.com.omnirent.rental.event.RentalLateEvent;
import br.com.omnirent.rental.event.RentalStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(
	    value = "app.mail.consumers.enabled",
	    havingValue = "true",
	    matchIfMissing = true
	)
@Component
@RequiredArgsConstructor
@RabbitListener(queues = "email.rental.queue", containerFactory = "rabbitListenerContainerFactory")
@Slf4j
public class EmailRentalConsumer {
	
    private final RentalEmailService rentalEmailService;
    
    private final JpaNotificationQueryRepository queryRepository;
    
    @RabbitHandler
    public void handle(RentalCreatedEvent event) {
    	RentalNotificationData notificationData =
    			getRentalNotificationData(event.entityId());
    	
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
    			getRentalNotificationData(event.entityId());
    	
    	rentalEmailService.sendRentalCanceledToOwner(notificationData);
    	rentalEmailService.sendRentalCanceledToRenter(notificationData);
    }
    
    @RabbitHandler
    public void handle(RentalExpiredEvent event) {
    	RentalNotificationData notificationData = 
    			getRentalNotificationData(event.entityId());

    	rentalEmailService.sendRentalExpiredToOwner(notificationData);
    	rentalEmailService.sendRentalExpiredToRenter(notificationData);
    }
    
    @RabbitHandler
    public void handle(RentalStatusChangedEvent event) {
    	RentalStatus newStatus = event.currentBody().status();
    	RentalNotificationData notificationData = null;
    	
    	switch (newStatus) {
    	case CONFIRMED:
    		notificationData = getRentalNotificationData(event.entityId());
    		rentalEmailService.sendRentalConfirmedToOwner(notificationData);
    		rentalEmailService.sendRentalConfirmedToRenter(notificationData);
    		break;

    	case PREPARING:
    		notificationData = getRentalNotificationData(event.entityId());
    		rentalEmailService.sendRentalPreparingToRenter(notificationData);
    		break;

    	case SHIPPED:
    		notificationData = getRentalNotificationData(event.entityId());
    		rentalEmailService.sendRentalShippedToRenter(notificationData);
    		break;

    	case RETURN_REQUESTED:
    		notificationData = getRentalNotificationData(event.entityId());
    		rentalEmailService.sendRentalReturnRequestedToOwner(notificationData);
    		rentalEmailService.sendRentalReturnRequestedToRenter(notificationData);
    		break;

    	case RETURN_SHIPPED:
    		notificationData = getRentalNotificationData(event.entityId());
    		rentalEmailService.sendRentalReturnShippedToOwner(notificationData);
    		rentalEmailService.sendRentalReturnShippedToRenter(notificationData);
    		break;

    	case RETURNED:
    		notificationData = getRentalNotificationData(event.entityId());
    		rentalEmailService.sendRentalReturnedToOwner(notificationData);
    		rentalEmailService.sendRentalReturnedToRenter(notificationData);
    		break;

    	default:
    		break;
    	}
    }
    
    private RentalNotificationData getRentalNotificationData(String rentalId) {
    	return queryRepository.findRentalNotificationData(rentalId)
    			.orElseThrow(() -> new NotificationDataNotException());
    }
  
}
