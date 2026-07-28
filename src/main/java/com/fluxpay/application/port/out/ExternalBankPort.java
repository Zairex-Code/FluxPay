package com.fluxpay.application.port.out;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface ExternalBankPort {
    Mono<Boolean> verifyExternalAccount(String accountId, BigDecimal amount);
}
