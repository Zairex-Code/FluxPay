package com.fluxpay.domain.event;

import com.fluxpay.domain.model.TransferStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferCreatedEvent(
        String id,
        String originAccount,
        String destinationAccount,
        BigDecimal amount,
        TransferStatus status,
        String description,
        Instant createdAt
) {
}
