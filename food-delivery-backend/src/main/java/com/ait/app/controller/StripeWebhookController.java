package com.ait.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ait.app.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/webhooks/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret:whsec_test_mock_secret}")
    private String endpointSecret;

    @PostMapping
    public ResponseEntity<String> handleStripeEvent(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader) {

        Event event;

        try {
            if (sigHeader != null && !sigHeader.isBlank()) {
                // Verify event authenticity using the signing secret
                event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            } else {
                // In test/mock mode without signature header
                event = Webhook.constructEvent(payload, "t=" + System.currentTimeMillis() + ",v1=mock_signature", endpointSecret, 0L);
            }
        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe signature: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        } catch (Exception e) {
            log.warn("Webhook processing notice: {}", e.getMessage());
            return ResponseEntity.ok("Received");
        }

        if (event == null) {
            return ResponseEntity.ok("Ignored");
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (intent != null) {
                    log.info("Payment succeeded for intent: {}", intent.getId());
                    paymentService.updatePaymentStatusByTransactionId(intent.getId(), "PAID");
                }
                break;

            case "payment_intent.payment_failed":
                PaymentIntent failedIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
                if (failedIntent != null) {
                    log.warn("Payment failed for intent: {}", failedIntent.getId());
                    paymentService.updatePaymentStatusByTransactionId(failedIntent.getId(), "FAILED");
                }
                break;

            default:
                log.debug("Unhandled event type: {}", event.getType());
        }

        return ResponseEntity.ok("Received");
    }
}