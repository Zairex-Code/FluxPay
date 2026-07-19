package com.fluxpay.domain.port.out.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.fluxpay.domain.port.out.r2dbc.entity.TransferEntity;

/**
 * 
 *  Spring Data R"DBC repository for reactive database operations
 */

@Repository
public interface TransferR2dbcRepository extends ReactiveCrudRepository<TransferEntity, String>{
    // Custom non-blocking queries can be defined here later using @Query
}
