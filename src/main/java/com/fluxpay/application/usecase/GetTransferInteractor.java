package com.fluxpay.application.usecase;

import com.fluxpay.application.port.in.GetTransferUseCase;
import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.model.Transfer;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class GetTransferInteractor implements GetTransferUseCase{
    
    private final TransferRepositoryPort transferRepositoryPort;

    @Override
    public Mono<Transfer> execute(String id) {
        return transferRepositoryPort.findById(id);
    }

    
    
}
