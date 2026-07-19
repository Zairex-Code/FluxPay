package com.fluxpay.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Transfer {
    private final String id;
    private final String sourceAccountId;
    private final String destinationAccountId;
    private final BigDecimal amount;
    private TransferStatus status;
    private final LocalDateTime createAt;
    

    public void process(){
        this.status = TransferStatus.PROCESSED;
    }

    public void fail(){
        this.status = TransferStatus.FAILED;
    }
}
