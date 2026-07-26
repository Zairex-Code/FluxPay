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

    @Column("desination_account")
    private String destinationAccount;

    private BigDecimal amount;

    private String status;

    private String description;

    @Column("created_at")
    private Instant createdAt;

    @Transient
    @Builder.Default
    private  boolean newEntity = true;

    @Override
    public boolean isNew(){
        return newEntity || id == null;
    }




}