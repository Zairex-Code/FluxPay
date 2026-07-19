package com.fluxpay.infrastructure.out.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Persistence Entity representing the database table structure.
 * we avoid @Data to prevent un predictable equals() and hashCode() behavior
 * which can cause severe issues in collections and persistence contexts
 * 
 * Persistence Entity representing the database table structure.
 * This class belongs strictly to the Infrastructure layer.
 */

@Getter
@Setter
@ToString
@Builder
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
