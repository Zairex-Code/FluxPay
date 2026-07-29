package com.fluxpay.application.port.out;

import com.fluxpay.domain.model.Transfer;
import reactor.core.publisher.Mono;

public interface TransferCachePort {
    Mono<Transfer> get(String id);
    Mono<Boolean> put(Transfer transfer);
}
