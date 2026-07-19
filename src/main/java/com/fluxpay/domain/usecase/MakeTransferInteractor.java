package com.fluxpay.domain.usecase;

import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.port.in.MakeTransferUseCase;
import com.fluxpay.domain.port.out.TransferRepositoryPort;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

/**
 * Domain service that implements the specific use case for making a transfer.
 * Notice the absence of Spring annotations. This is pure Java.
 */
@RequiredArgsConstructor
public class MakeTransferInteractor implements MakeTransferUseCase {

    private final TransferRepositoryPort transferRepositoryPort;

    @Override
    public Mono<Transfer> execute(Transfer transfer) {
        // Here we will eventually add business validations
        // For example: Checking sufficient funds, verifying account status, etc.
        
        // For now, we simply set an initial status and delegate to the outbound port.
        // We use Reactive Streams (Mono) to ensure non-blocking execution.
        return Mono.just(transfer)
                // .flatMap(t -> validateAccounts(t)) -> SAGA step in the future
                .flatMap(transferRepositoryPort::save);
    }
}