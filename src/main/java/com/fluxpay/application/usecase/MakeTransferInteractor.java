package com.fluxpay.application.usecase;

import java.time.LocalDateTime;

import com.fluxpay.application.port.in.MakeTransferUseCase;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;

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

        transfer.setStatus(TransferStatus.PENDING);
        transfer.setCreatedAt(LocalDateTime.now());

        return Mono.just(transfer)
                // .flatMap(t -> validateAccounts(t)) -> SAGA step in the future
                .flatMap(transferRepositoryPort::save);
    }
}