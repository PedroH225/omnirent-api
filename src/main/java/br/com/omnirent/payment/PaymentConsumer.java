package br.com.omnirent.payment;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import br.com.omnirent.payment.event.PaymentRequestedEvent;
import lombok.RequiredArgsConstructor;

@ConditionalOnProperty(
	    value = "app.payment.consumers.enabled",
	    havingValue = "true",
	    matchIfMissing = true
	)
@Component
@RequiredArgsConstructor
@RabbitListener(queues = "payment.queue")
public class PaymentConsumer {

	private final PaymentService paymentService;
	
    @RabbitHandler
	public void handle(PaymentRequestedEvent event) {
		paymentService.createPayment(event);
	}
}
