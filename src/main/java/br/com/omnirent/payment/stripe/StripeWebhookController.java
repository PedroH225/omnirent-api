package br.com.omnirent.payment.stripe;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.SignatureVerificationException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class StripeWebhookController {

	private final StripeWebhookService stripeService;
	
    @PostMapping
    public ResponseEntity<Void> handle(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signature) {

    	stripeService.handle(payload, signature);

        return ResponseEntity.ok().build();
    }
}

