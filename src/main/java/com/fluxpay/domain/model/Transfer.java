package com.fluxpay.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Builder(toBuilder = true)
public class Transfer {
    private final String id;
    private final String originAccount;
    private final String destinationAccount;
    private final BigDecimal amount;
    private final TransferStatus status;
    private final String description;
    private final Instant createdAt;


    public boolean isSameAccount(){
        return originAccount != null && originAccount.equalsIgnoreCase(destinationAccount);
    }

    public boolean isAmountInvalid(){
        return amount == null || amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    public Transfer markAsCompleted(){
        return this.toBuilder()
                .status(TransferStatus.COMPLETED)
                .build();
    }

    public Transfer MarkAsFailed(){
        return this.toBuilder()
                .status(TransferStatus.FAILED)
                .build();
    }

}
