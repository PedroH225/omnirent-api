package br.com.omnirent.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.PaymentStatus;
import br.com.omnirent.exception.infrastructure.NotificationDataNotException;
import br.com.omnirent.notification.JpaNotificationQueryRepository;
import br.com.omnirent.notification.context.PaymentNotificationData;
import br.com.omnirent.notification.email.service.PaymentEmailService;
import br.com.omnirent.payment.event.PaymentConfirmedEvent;
import br.com.omnirent.payment.event.PaymentCreatedEvent;
import br.com.omnirent.payment.event.PaymentStatusChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(
	    value = "app.mail.consumers.enabled",
	    havingValue = "true",
	    matchIfMissing = true
	)
@Component
@RequiredArgsConstructor
@RabbitListener(queues = "email.payment.queue", containerFactory = "rabbitListenerContainerFactory")
@Slf4j
public class EmailPaymentConsumer {
	
	private final PaymentEmailService emailService;
	
    private final JpaNotificationQueryRepository queryRepository;
	
    @RabbitHandler
    public void handle(PaymentCreatedEvent event) {
    	emailService.sendPaymentCreated(getNotificationData(event.entityId()));
    }
    
    @RabbitHandler
    public void handle(PaymentConfirmedEvent event) {
    	emailService.sendPaymentConfirmed(getNotificationData(event.entityId()));
    }
    
    @RabbitHandler
    public void handle(PaymentStatusChangedEvent event) {
    	switch (event.currentBody().status()) {
		case REFUNDED:
			emailService.sendRefundConfirmed(getNotificationData(event.entityId()));
			break;
		default:
			break;
		}
    }
    
    private PaymentNotificationData getNotificationData(String paymentId) {
    	return queryRepository
    			.findPaymentNotificationData(paymentId)
    			.orElseThrow(() -> new NotificationDataNotException());
    }
}
