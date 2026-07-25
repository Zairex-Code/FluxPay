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

/**
 * Domain service that implements the specific use case for making a transfer.
 * Notice the absence of Spring annotations. This is pure Java.
 */

@Slf4j
@RequiredArgsConstructor
public class MakeTransferInteractor implements MakeTransferUseCase {

    private final TransferRepositoryPort transferRepositoryPort;

    @Override
    public Mono<Transfer> execute(Transfer transfer) {

        log.debug("Initiating transfer processing from origin account: {}", 
                    transfer != null ? transfer.getOriginAccount() : "null"
                );

        return Mono.justOrEmpty(transfer)
            .switchIfEmpty(Mono.error(new InvalidTransferException("Transfer request payload cannot be null"))) //If the input object is null, emit a Mono.error
            .flatMap(this::validateTransfer)
            .flatMap(transferRepositoryPort::save)
            .doOnSuccess(savedTransfer -> 
                            log.info("Transfer successfully executed with ID: {}", savedTransfer.getId()))
            .onErrorResume(InvalidTransferException.class, ex -> {
                log.error("Domain business rule violation: {} ", ex.getMessage());
                return Mono.error(ex);
            });
        
    }

    private Mono<Transfer>  validateTransfer(Transfer transfer){
        if(transfer.getAmount()== null || transfer.getAmount().compareTo(BigDecimal.ZERO) <= 0 ){
            return Mono.error(new InvalidTransferException("Transfer amount must be strictly greater than zero"));
        }

        if(transfer.getOriginAccount() != null && transfer.getOriginAccount().equals(transfer.getDestinationAccount())){
            return Mono.error(new InvalidTransferException("Source and target account cannot be the same"));
        }

        return Mono.just(transfer);

    }
}