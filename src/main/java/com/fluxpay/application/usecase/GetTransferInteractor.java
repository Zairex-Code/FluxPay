package com.fluxpay.application.usecase;

import com.fluxpay.application.port.in.GetTransferUseCase;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.exception.InvalidTransferException;
import com.fluxpay.domain.model.Transfer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Application Interactor responsible for querying transfer records by ID
 */
@Slf4j
@RequiredArgsConstructor
public class GetTransferInteractor implements GetTransferUseCase{
    
    private final TransferRepositoryPort transferRepositoryPort;

    /**
    *Retrieves a single transfer by its unique identifier
    * <p>
    * Validates that the input ID is not blank before querying the repository port
    *
    * @param id The unique transfer identifier string
    * @return A Mono emitting the found Transfer entity, or empty if not found
    * */

    @Override
    public Mono<Transfer> execute(String id) {
        log.debug("Fetching transfer details for ID: {}", id);


        return Mono.justOrEmpty(id)
                .filter(transferId -> !transferId.isBlank())
                .switchIfEmpty(Mono.error(new InvalidTransferException("Transfer ID cannot be null or empty")))
                .flatMap(transferRepositoryPort::findById)
                .doOnSuccess(transfer -> {
                    if (transfer == null){
                        log.debug("No transfer found with ID: {}", id);
                    }else {
                        log.debug("Successfully retrieved transfer with ID: {}", id);
                    }
                });

    }

    
    
}
