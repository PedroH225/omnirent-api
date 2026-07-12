package br.com.omnirent.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.exception.infrastructure.NotificationDataNotException;
import br.com.omnirent.notification.JpaNotificationQueryRepository;
import br.com.omnirent.notification.context.PaymentNotificationData;
import br.com.omnirent.notification.email.service.PaymentEmailService;
import br.com.omnirent.payment.event.PaymentCreatedEvent;
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
    	PaymentNotificationData notificationData = queryRepository
    			.findPaymentNotificationData(event.entityId())
    			.orElseThrow(() -> new NotificationDataNotException());
    	
    	emailService.sendPaymentCreated(notificationData);
    }
}
