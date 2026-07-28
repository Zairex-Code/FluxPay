package com.fluxpay.application.usecase;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fluxpay.application.port.in.MakeTransferUseCase;
import com.fluxpay.application.port.out.ExternalBankPort;
import com.fluxpay.application.port.out.TransferEventPort;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.event.TransferCreatedEvent;
import com.fluxpay.domain.exception.InvalidTransferException;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/*
* Application Interactor that orchestrates the execution of bank transfers
*
* Handle requests validation, state transactions (marking as completed)
* reactive persistence,and operational logins
* */

@Slf4j
@Service
@RequiredArgsConstructor
public class MakeTransferInteractor implements MakeTransferUseCase {

    private final TransferRepositoryPort transferRepositoryPort;
    private final ExternalBankPort externalBankPort;
    private final TransferEventPort transferEventPort;

    /**
     * Execute a new Money Transfer transaction reactively
     * Validates domain invariants, performs external bank checks, marks the transfer as completed,
     * persists the entity via the repository port, emits a domain event to kafka , and records operational logs
     *
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
                .flatMap(validTransfer -> externalBankPort.verifyExternalAccount(
                        validTransfer.getOriginAccount(),
                        validTransfer.getAmount()
                ).thenReturn(validTransfer))
                .map(Transfer::markAsCompleted)
                .flatMap(transferRepositoryPort::save)
                .flatMap(savedTransfer -> {
                    TransferCreatedEvent event = new TransferCreatedEvent(
                            savedTransfer.getId(),
                            savedTransfer.getOriginAccount(),
                            savedTransfer.getDestinationAccount(),
                            savedTransfer.getAmount(),
                            savedTransfer.getStatus(),
                            savedTransfer.getDescription(),
                            savedTransfer.getCreatedAt()
                    );
                    return transferEventPort.publishTransferCreated(event)
                            .thenReturn(savedTransfer);
                })
                .doOnSuccess(savedTransfer -> log.info("Transfer successfully execute with: {}", savedTransfer.getId()))
                .doOnError(InvalidTransferException.class, ex -> log.error("Domain bussines rule violation: {}", ex.getMessage()));
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