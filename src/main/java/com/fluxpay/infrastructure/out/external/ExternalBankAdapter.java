package com.fluxpay.infrastructure.out.external;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class ExternalBankAdapter {
    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;



}
