package br.com.omnirent.payment;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.common.enums.RentalStatus;
import br.com.omnirent.payment.event.PaymentExpirationRequestEvent;
import br.com.omnirent.payment.event.PaymentRequestedEvent;
import br.com.omnirent.rental.event.RentalCanceledEvent;
import br.com.omnirent.rental.event.RentalLateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ConditionalOnProperty(
	    value = "app.payment.consumers.enabled",
	    havingValue = "true",
	    matchIfMissing = true
	)
@Component
@RequiredArgsConstructor
@RabbitListener(queues = "payment.queue", containerFactory = "rabbitListenerContainerFactory")
@Slf4j
public class PaymentConsumer {

	private final PaymentService paymentService;
	
    @RabbitHandler
	public void handle(PaymentRequestedEvent event) {
		paymentService.createPayment(event);
	}
    
    @RabbitHandler
    public void handle(RentalCanceledEvent event) {
    	RentalStatus oldStatus = event.previousBody().status();
    	if (oldStatus == RentalStatus.CREATED) {
			paymentService.cancelPayment(event.entityId(), event.actorId());
    	}
    	if (oldStatus == RentalStatus.CONFIRMED) {
			paymentService.requestRefund(event.entityId(), event.actorId());
		}
    }
    
    @RabbitHandler
    public void handle(PaymentExpirationRequestEvent expirationRequest) {
    	paymentService.expirePayment(expirationRequest.paymentId());
    }
    
    @RabbitHandler
    public void handle(RentalLateEvent event) {
    	paymentService.restartPaymentFlow(event.rentalId());
    }
}
