package com.fluxpay.infrastructure.out.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


/**
 * R2DBC database entity mapped to the 'transfers' table
 *
 * Implements Persistable to explicitly manage entity newness state
 * when client-generated UUIDs are used
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"amount"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table("transfers") 
public class TransferEntity implements Persistable<UUID>{

    @Id
    @EqualsAndHashCode.Include
    private UUID id;

    @Column("origin_account")
    private String originAccount;

    @Column("destination_account")
    private String destinationAccount;

    private BigDecimal amount;

    private String status;

    private String description;

    @Column("created_at")
    private Instant createdAt;

    @Transient
    @Builder.Default
    private  boolean newEntity = true;


    /**
     * Determines whether this is new (requires SQL INSERT) or existing (requires SQL UPDATE)
     * @return true if new entity or ID is null; false otherwise
     */

    @Override
    public boolean isNew(){
        return newEntity || id == null;
    }




}