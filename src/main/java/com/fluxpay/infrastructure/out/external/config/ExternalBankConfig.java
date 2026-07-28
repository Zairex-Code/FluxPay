package com.fluxpay.infrastructure.out.external.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExternalBankConfig {
    @Value("${external.bank.url:https://api.externalbank.com}")
    private String externalBankUrl;

    @Bean
    public WebClient externalBankWebClient(WebClient.Builder webClientBuilder){
        return webClientBuilder
                .baseUrl(externalBankUrl)
                .build();
    }

    @Bean
    public CircuitBreaker externalBankCircuitBreaker(CircuitBreakerRegistry registry){
        return registry.circuitBreaker("externalBankService");
    }
}
