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

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"amount"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table("transfers") 
public class TransferEntity {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    @Column("origin_account")
    private String originAccount;

    @Column("desination_account")
    private String destinationAccount;

    private BigDecimal amount;

    private String status;

    private String description;

    private Instant createdAt;

}