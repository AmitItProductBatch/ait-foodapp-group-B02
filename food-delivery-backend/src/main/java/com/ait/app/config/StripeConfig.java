package com.ait.app.config;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.api-base:}")
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