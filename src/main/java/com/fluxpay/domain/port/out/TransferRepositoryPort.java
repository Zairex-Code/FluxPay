package com.fluxpay.domain.port.out;

import com.fluxpay.domain.model.Transfer;

import reactor.core.publisher.Mono;

public interface TransferRepositoryPort {
    /**
     * Saves a transfer into underlying persistence mechanism
     * 
     * @param transfer The transfer domain entity
     * @return A Mono emitting the saved transfer.
     */
    Mono<Transfer> save(Transfer transfer);

    /**
     * Finds a transfer by its unique identifier
     * 
     * @param id The unique identifier of the transfer
     * @return A Mono emitting the found transfer, or empty if not found.
     */

    Mono<Transfer> findById(String id);


}
