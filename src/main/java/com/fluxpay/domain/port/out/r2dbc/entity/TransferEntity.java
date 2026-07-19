package com.fluxpay.domain.port.out.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("transfer")
public class TransferEntity {
    
    @Id
    private String id;
    private String originAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    
}
