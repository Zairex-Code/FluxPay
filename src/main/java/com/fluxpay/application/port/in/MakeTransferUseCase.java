package com.fluxpay.application.port.in;

import com.fluxpay.domain.model.Transfer;

import reactor.core.publisher.Mono;

public interface MakeTransferUseCase {
    
    /**
     * Excecute the transfer process.
     * 
     * @param transfer The transfer data to be processed
     * @return A mono emitting the processed transfer with its updated state.
     */
    Mono<Transfer> execute(Transfer transfer);
}
