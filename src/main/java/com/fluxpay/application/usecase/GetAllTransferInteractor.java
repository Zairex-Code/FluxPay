package com.fluxpay.application.usecase;

import com.fluxpay.application.port.in.GetAllTransfersUseCase;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.model.Transfer;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAllTransferInteractor implements GetAllTransfersUseCase{
    private final TransferRepositoryPort transferRepositoryPort;

    @Override
    public Flux<Transfer> execute() {
        return transferRepositoryPort.findAll();
    }

    
}
