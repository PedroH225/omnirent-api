package br.com.omnirent.payment.stripe;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import com.stripe.model.checkout.Session;

import br.com.omnirent.payment.PaymentService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeWebhookService {

	@Value("${stripe.webhook.secret}")
	private String endpointSecret;

	private final PaymentService paymentService;

	public void handle(String payload, String signature) {
		try {
			Event event = Webhook.constructEvent(payload, signature, endpointSecret);

			switch (event.getType()) {

			case "checkout.session.completed" -> handleCheckoutCompleted(event);
			
			default -> {}
			}
		} catch (SignatureVerificationException e) {
			e.printStackTrace();
		}

	}

	private void handleCheckoutCompleted(Event event) {
		Session session = (Session) event.getDataObjectDeserializer()
				.getObject()
				.orElseThrow();

		paymentService.confirmPayment(session.getId());
	}
}
