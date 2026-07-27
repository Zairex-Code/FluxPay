package com.fluxpay.application.usecase;

import com.fluxpay.application.port.in.GetAllTransfersUseCase;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.model.Transfer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RequiredArgsConstructor
public class GetAllTransferInteractor implements GetAllTransfersUseCase{
    private final TransferRepositoryPort transferRepositoryPort;

    @Override
    public Flux<Transfer> execute() {
        log.debug("Fetching all recorded transfers");

        return transferRepositoryPort.findAll();
    }

    
}
