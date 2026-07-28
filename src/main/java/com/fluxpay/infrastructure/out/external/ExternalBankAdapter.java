package com.fluxpay.infrastructure.out.external;


import com.fluxpay.application.port.out.ExternalBankPort;
import com.fluxpay.domain.exception.InvalidTransferException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

/**
 * Outbound infrastructure adapter responsible for communicating with third-party core services
 * Implements ExternalBankPort and integrates Resilience4j Circuit Breaker
 * for reactive fault tolerance without blocking the Event Loop
 */

@Slf4j
@RequiredArgsConstructor
@Component
public class ExternalBankAdapter implements ExternalBankPort {
    private final WebClient externalBankWebClient;
    private final CircuitBreaker externalBankCircuitBreaker;

    /**
     * Reactively verifies account balance availability against and external core bank
     *
     * @param accountId Target account string to verify
     * @param amount Amount to validate
     * @return Mono emitting true if account balance is valid, or mapping to a Domain Exception if failure occurs
     */


    @Override
    public Mono<Boolean> verifyExternalAccount(String accountId, BigDecimal amount) {
        return externalBankWebClient.get()
                .uri("/accounts/{id}/balance?amount={amount}", accountId, amount)
                .retrieve()
                .bodyToMono(Boolean.class)
                .transformDeferred(CircuitBreakerOperator.of(externalBankCircuitBreaker))
                .doOnError(throwable -> log.warn(
                        "Circuit Breaker OPEN or exter bank service down. Account [{}]. Reason: {}",accountId,throwable.getMessage()
                ))
                .onErrorMap(throwable -> new InvalidTransferException(
                        "External validation service is temporarily unavailable. Please try again later."
                ));
    }
}
