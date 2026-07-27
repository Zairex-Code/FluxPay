package com.fluxpay.infrastructure.out.r2dbc.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.fluxpay.infrastructure.out.r2dbc.entity.TransferEntity;

import java.util.UUID;

/**
 *  Spring Data R2DBC repository interface for reactive CRUD database operations
 */


public interface TransferR2dbcRepository extends ReactiveCrudRepository<TransferEntity, UUID>{

}
