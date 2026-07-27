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

    /**
     * Saves a transfer domain entity into PostgresSQL via R2DBC reactively
     * @param transfer The transfer domain entity
     * @return A mono emitting the persisted Transfer domain instance
     */

    @Override
    public Mono<Transfer> save(Transfer transfer) {
        return Mono.justOrEmpty(transfer)
                .map(this::toEntity)
                .flatMap(r2dbcRepository::save)
                .map(this::toDomain)
                .doOnSuccess(saved -> log.debug("Transfer successfully persisted with ID: {}", saved.getId()))
                .doOnError(error -> log.error("Failed to persist transfer in R2DBC database: {}", error.getMessage()));
    }

    /**
     * Finds a transfer by its unique string identifier
     *
     * @param id The unique identifier of the transfer
     * @return  A mono emitting the found Transfer, or empty if not found or invalid UUID
     */

    @Override
    public Mono<Transfer> findById(String id) {
        return Mono.justOrEmpty(parseUuid(id))
                .flatMap(r2dbcRepository::findById)
                .map(this::toDomain)
                .doOnError(error -> log.error("Failed to retrieve transfer by ID [{}]: {}", id, error.getMessage()));
    }


    /**
     * Fetches all transfers recorded in the database
     *
     * @return A Flux emitting all Transfer domain entities
     */
    @Override
    public Flux<Transfer> findAll() {
        return r2dbcRepository.findAll()
                .map(this::toDomain)
                .doOnError(error -> log.error("Failed to fetch transfers from R2DBC : {}", error.getMessage()));
    }


    /**
     * Converts a Domain Transfer model to a Database Transfer Entity
     *
     * Ensures proper UUID generation and flags 'isNew' correctly for Spring Data R2DBC inserts
     */
    private TransferEntity toEntity(Transfer domain){
        UUID entityId = parseUuid(domain.getId());
        boolean isNew = false;

        if (entityId == null){
            entityId = UUID.randomUUID();
            isNew = true;
        }

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

    /**
     * Converts a Database TransferEntity to Domain Transfer model
     */
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

    /**
     * safely  parses a string into UUID
     */
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

    /**
     * safely converts a database string to a TransferStatus enum
     */
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
