package com.fluxpay.domain.usecase;

import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.port.in.GetTransferUseCase;
import com.fluxpay.domain.port.out.TransferRepositoryPort;

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
