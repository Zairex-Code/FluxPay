package com.fluxpay.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transfer {
    private String id;
    private String originAccount;
    private String destinationAccount;
    private BigDecimal amount;
    private TransferStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    

    public void markAsCompleted(){
        if(this.status != TransferStatus.FUNDS_RESERVED){
            throw new IllegalStateException("Transfer cannot be completed if funds are not reserved first.");
        }

        this.status = TransferStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsReversed(){
        this.status = TransferStatus.REVERSED;
        this.updatedAt = LocalDateTime.now();
    }
}
