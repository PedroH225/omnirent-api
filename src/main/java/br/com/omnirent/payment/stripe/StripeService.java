package br.com.omnirent.payment.stripe;

import org.springframework.stereotype.Service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import br.com.omnirent.config.properties.StripeProperties;
import br.com.omnirent.payment.dto.StripeCheckoutSession;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeService {

    private final StripeProperties stripeProperties;

    @PostConstruct
    public void init() {
       Stripe.apiKey = stripeProperties.secretKey();
    }

    public StripeCheckoutSession createCheckoutSession(
            long amount, String currency, 
            String successUrl, String cancelUrl,String referenceId) {
        try {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(successUrl)
                            .setCancelUrl(cancelUrl)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency(currency)
                                                            .setUnitAmount(amount)
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("OmniRent Payment")
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .putMetadata("payment_reference", referenceId)
                            .build();

            Session session = Session.create(params);

            return new StripeCheckoutSession(session.getId(), session.getUrl());
            
        } catch (Exception e) {
            throw new RuntimeException("Error creating Stripe checkout session", e);
        }
    }
    
    public void requestRefund(String paymentId, String paymentIntentId) {
        try {
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .putMetadata("payment_reference", paymentId)
                .build();

			Refund.create(params);
		} catch (StripeException e) {
			e.printStackTrace();
		}
    }
}
