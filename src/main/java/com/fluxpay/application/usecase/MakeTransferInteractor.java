package com.fluxpay.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fluxpay.application.port.in.MakeTransferUseCase;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.exception.InvalidTransferException;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/*
* Application Interactor that orchestrates the execution of bank transfers
*
* Handle requests validation, state transactions (marking as completed)
* reactive persistence,and operational loggins
* */

@Slf4j
@RequiredArgsConstructor
public class MakeTransferInteractor implements MakeTransferUseCase {

    private final TransferRepositoryPort transferRepositoryPort;

    /**
     * Execute a new Money Transfer transaction reactively
     * Validates domain invariants, marks the transfer as completed, persists the entity via
     * the repository port, and records operational logs
     * @Param transfer The domain model containing transfer request payload
     * @return A mono emitting the saved and completed Transfer domain model
     * */

    @Override
    public Mono<Transfer> execute(Transfer transfer) {

        log.debug("Initiating transfer processing from origin account: {}", 
                    transfer != null ? transfer.getOriginAccount() : "null"
                );

        return Mono.justOrEmpty(transfer)
            .switchIfEmpty(Mono.error(new InvalidTransferException("Transfer request payload cannot be null"))) //If the input object is null, emit a Mono.error
            .flatMap(this::validateTransfer)
            .map(Transfer::markAsCompleted)
            .flatMap(transferRepositoryPort::save)
            .doOnSuccess(savedTransfer -> log.info("Transfer successfully executed with ID: {}", savedTransfer.getId()))
            .doOnError(InvalidTransferException.class, ex -> log.error("Domain business rule violation: {}", ex.getMessage()));
        
    }


    /*
    * Validates internal transfer invariants before processing
    *
    * @param transfer instance to validate
    * @return A mono containing the validated transfer, or an error if invalid
    * */
    private Mono<Transfer>  validateTransfer(Transfer transfer){
        if(transfer.isAmountInvalid()){
            return Mono.error(new InvalidTransferException("Transfer amount must be strictly greater than zero"));
        }

        if(transfer.isSameAccount()){
            return Mono.error(new InvalidTransferException("Source and target account cannot be the same"));
        }

        return Mono.just(transfer);

    }
}