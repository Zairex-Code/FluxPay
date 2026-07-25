package com.fluxpay.infrastructure.in.web.dto;

import com.fluxpay.domain.model.Transfer;
import com.fluxpay.infrastructure.security.MaskingUtils;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(
    String id,
    String originAccount,
    String destinationAccount,
    BigDecimal amount,
    String status,
    Instant createdAt
) {
    // Static factory method to map Domain model into an inmutable DTO with PII masking
    public static TransferResponse fromDomain(Transfer domain){
        return new TransferResponse(
                domain.getId(),
                MaskingUtils.maskAccount(domain.getOriginAccount()),
                MaskingUtils.maskAccount(domain.getDestinationAccount()),
                domain.getAmount(),
                domain.getStatus() != null ? domain.getStatus().name() : null,
                domain.getCreatedAt()
        );
    }
}
