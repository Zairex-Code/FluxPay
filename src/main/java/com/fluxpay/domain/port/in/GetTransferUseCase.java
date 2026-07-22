package com.fluxpay.domain.port.in;

import com.fluxpay.domain.model.Transfer;

import reactor.core.publisher.Mono;

public interface GetTransferUseCase {
    Mono<Transfer> execute(String id);
}
