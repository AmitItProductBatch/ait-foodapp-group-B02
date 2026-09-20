package com.ait.app.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api-key:sk_test_123}")
    private String apiKey;

    @Value("${stripe.api-base:http://194.242.57.93:12111}")
    private String apiBase;

    @PostConstruct
    public void initStripe() {
        // Set API key
        Stripe.apiKey = apiKey;

        // If a mock URL or custom base is provided, override default https://api.stripe.com
        if (apiBase != null && !apiBase.isBlank()) {
            Stripe.overrideApiBase(apiBase);
        }
    }
}