package com.fluxpay.infrastructure.out.r2dbc;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;
import com.fluxpay.infrastructure.out.r2dbc.entity.TransferEntity;
import com.fluxpay.infrastructure.out.r2dbc.repository.TransferR2dbcRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TransferRepositoryAdapter implements TransferRepositoryPort{

    private final TransferR2dbcRepository repository;

    @Override
    public Mono<Transfer> save(Transfer transfer) {

        TransferEntity entity = TransferEntity.builder()
                                                    .id(transfer.getId())
                                                    .originAccount(transfer.getOriginAccount())
                                                    .destinationAccount(transfer.getDestinationAccount())
                                                    .amount(transfer.getAmount())
                                                    .status(transfer.getStatus().name())
                                                    .createdAt(transfer.getCreatedAt() != null ? transfer.getCreatedAt() : LocalDateTime.now())
                                                    .build();

        

        
        return repository.save(entity).map(this::toDomain);
    }

    @Override
    public Mono<Transfer> findById(String id) {
        return repository.findById(id)
                                .map(this::toDomain);
    }

    private Transfer toDomain(TransferEntity entity) {
        // Utilizamos el Patrón Builder para reconstituir el objeto
        // de forma inmutable, sin utilizar un solo setter.
        return Transfer.builder()
                .id(entity.getId())
                .originAccount(entity.getOriginAccount())
                .destinationAccount(entity.getDestinationAccount())
                .amount(entity.getAmount())
                .status(TransferStatus.valueOf(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    

    
}
