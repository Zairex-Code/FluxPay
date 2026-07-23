package com.fluxpay.application.port.in;

import com.fluxpay.domain.model.Transfer;

import reactor.core.publisher.Flux;

public interface GetAllTransfersUseCase {
    Flux<Transfer> execute();
}

