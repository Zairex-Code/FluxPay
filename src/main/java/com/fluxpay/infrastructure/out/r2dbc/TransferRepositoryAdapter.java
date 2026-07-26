package com.fluxpay.infrastructure.out.r2dbc;

import java.time.Instant;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fluxpay.application.port.out.TransferRepositoryPort;
import com.fluxpay.domain.model.Transfer;
import com.fluxpay.domain.model.TransferStatus;
import com.fluxpay.infrastructure.out.r2dbc.entity.TransferEntity;
import com.fluxpay.infrastructure.out.r2dbc.repository.TransferR2dbcRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransferRepositoryAdapter implements TransferRepositoryPort{

    private final TransferR2dbcRepository r2dbcRepository;

    @Override
    public Mono<Transfer> save(Transfer transfer) {
        return Mono.justOrEmpty(transfer)
                .map(this::toEntity)
                .flatMap(r2dbcRepository::save)
                .map(this::toDomain)
                .doOnSuccess(saved -> log.debug("Transfer successfully persisted with ID: {}", saved.getId()))
                .doOnError(error -> log.error("Failed to persist transfer in R2DBC database: {}", error.getMessage()));
    }

    @Override
    public Mono<Transfer> findById(String id) {
        return Mono.justOrEmpty(parseUuid(id))
                .flatMap(r2dbcRepository::findById)
                .map(this::toDomain)
                .doOnError(error -> log.error("Failed to retrieve transfer by ID [{}]: {}", id, error.getMessage()));
    }


    @Override
    public Flux<Transfer> findAll() {

        return r2dbcRepository.findAll()
                .map(this::toDomain)
                .doOnError(error -> log.error("Failed to fetch transfers from R2DBC : {}", error.getMessage()));
    }


    private TransferEntity toEntity(Transfer domain){
        UUID entityId = parseUuid(domain.getId());
        boolean isNew = (entityId == null);

        return TransferEntity.builder()
                .id(entityId)
                .originAccount(domain.getOriginAccount())
                .destinationAccount(domain.getDestinationAccount())
                .amount(domain.getAmount())
                .status(domain.getStatus() != null ? domain.getStatus().name() : TransferStatus.PENDING.name() )
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : Instant.now())
                .newEntity(isNew)
                .build();
    }

    private Transfer toDomain(TransferEntity entity){
        return Transfer.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .originAccount(entity.getOriginAccount())
                .destinationAccount(entity.getDestinationAccount())
                .amount(entity.getAmount())
                .status(parseStatus(entity.getStatus()))
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    private UUID parseUuid(String uuidStr){
        if (uuidStr == null || uuidStr.isBlank()){
            return null;
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e){
            log.warn("Invalid UUID format encountered'{}'. Returning null safely.", uuidStr);
            return null;
        }
    }

    private TransferStatus parseStatus(String statusStr){
        if (statusStr == null || statusStr.isBlank()){
            return TransferStatus.PENDING;
        }
        try {
            return TransferStatus.valueOf(statusStr);
        }catch (IllegalArgumentException e){
            log.warn("Unrecognized TransferStatus value in database: '{}'. Defaulting to PENDING.", statusStr);
            return TransferStatus.PENDING;
        }
    }




}
