package br.com.omnirent.payment.stripe;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import br.com.omnirent.config.properties.StripeProperties;
import br.com.omnirent.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    private final PaymentService paymentService;
    
    private final StripeProperties stripeProperties;

	public void handle(String payload, String signature) {
		try {
			Event event = Webhook.constructEvent(
					payload, signature, stripeProperties.webhookSecret());	
			
			if (event.getType().equals("checkout.session.completed")) {
		        handleCheckoutCompleted(payload);
		    }
			
		} catch (SignatureVerificationException e) {
			log.error("""
					Cause: {}
					Message: {}
					""", e.getCause(), e.getMessage());
		}

	}

	private void handleCheckoutCompleted(String payload) {
	    JsonObject root = JsonParser.parseString(payload).getAsJsonObject();

	    JsonObject data = root
	            .getAsJsonObject("data")
	            .getAsJsonObject("object");

	    String paymentId = data
	            .getAsJsonObject("metadata")
	            .get("payment_reference")
	            .getAsString();
	    
	    String paymentIntent = data
	            .get("payment_intent")
	            .getAsString();

	    paymentService.confirmPayment(paymentId, paymentIntent);
	}
}
