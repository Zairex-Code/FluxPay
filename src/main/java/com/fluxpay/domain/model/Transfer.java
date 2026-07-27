package com.fluxpay.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class Transfer {
    private final String id;
    private final String originAccount;
    private final String destinationAccount;
    private final BigDecimal amount;
    private final TransferStatus status;
    private final String description;
    private final Instant createdAt;



    // Factory method for creating an initial domain model
    public static Transfer createInitial(String originAccount, String destinationAccount, BigDecimal amount, String description){
        return Transfer.builder()
                .originAccount(originAccount)
                .destinationAccount(destinationAccount)
                .amount(amount)
                .description(description)
                .status(TransferStatus.PENDING)
                .createdAt(Instant.now())
                .build();
    }

    // Rich Domain Behaviors (DDD Invariants & State Transitions)

    // Immutable SAGA State Transitions
    public Transfer markAsProcessing(){
        return this.toBuilder().status(TransferStatus.PROCESSING).build();
    }

    public Transfer reserveFunds(){
        return this.toBuilder().status(TransferStatus.FUNDS_RESERVED).build();
    }

    public Transfer markAsCompleted(){
        return this.toBuilder().status(TransferStatus.COMPLETED).build();
    }

    public Transfer markAsFailed(){
        return this.toBuilder().status(TransferStatus.FAILED).build();
    }

    public Transfer reverse(){
        return this.toBuilder().status(TransferStatus.REVERSED).build();
    }


    // Invariant business rules
    public boolean isSameAccount(){
        return originAccount != null && originAccount.equalsIgnoreCase(destinationAccount);
    }

    public boolean isAmountInvalid(){
        return amount == null || amount.compareTo(BigDecimal.ZERO) <= 0;
    }

}
